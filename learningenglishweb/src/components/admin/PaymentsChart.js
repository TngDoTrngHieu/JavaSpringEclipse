import React, { useMemo } from "react";
import { Card } from "react-bootstrap";
import {
    ResponsiveContainer,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
} from "recharts";

const parseDate = (p) => {
    const dstr = p.createdAt || p.created_at || p.date || p.paymentDate || p.timestamp || p.time || null;
    if (!dstr) return null;
    const d = new Date(dstr);
    return isNaN(d) ? null : d;
};

const getAmount = (p) => {
    if (typeof p.amount === "number") return p.amount;
    if (p.amount && !isNaN(Number(p.amount))) return Number(p.amount);
    if (p.total && !isNaN(Number(p.total))) return Number(p.total);
    if (p.price && !isNaN(Number(p.price))) return Number(p.price);
    return 0;
};

// Lấy danh sách 12 tháng của một năm cụ thể (VD: 2026-01 -> 2026-12)
const getMonthsOfYear = (year) => {
    const keys = [];
    for (let i = 1; i <= 12; i++) {
        keys.push(`${year}-${String(i).padStart(2, "0")}`);
    }
    return keys;
};

const aggregateByMonth = (payments, year) => {
    const keys = getMonthsOfYear(year);
    const map = {};
    keys.forEach((k) => (map[k] = 0));

    payments.forEach((p) => {
        const d = parseDate(p);
        if (!d) return;
        const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;

        // Chỉ cộng tiền nếu key trùng với tháng trong năm đang xét
        if (key in map) {
            map[key] += getAmount(p);
        }
    });

    // Map lại để có label đẹp (Tháng 1 -> T1, Tháng 12 -> T12)
    return keys.map((k, index) => ({
        monthLabel: `T${index + 1}`, // Dùng để hiển thị trục X
        fullDate: k,                 // Dùng nếu cần tooltip chi tiết
        amount: Math.round(map[k] * 100) / 100
    }));
};

const PaymentsChart = ({ payments = [], year = new Date().getFullYear() }) => {
    const data = useMemo(() => aggregateByMonth(payments, year), [payments, year]);

    const total = useMemo(() => data.reduce((s, r) => s + (r.amount || 0), 0), [data]);

    return (
        <Card className="shadow-sm border-0">
            <Card.Body>
                <div className="d-flex justify-content-between align-items-center mb-2">
                    <div>
                        <div className="text-muted small">Doanh thu năm {year}</div>
                        <div className="fw-semibold text-success fs-5">
                            Tổng: {total.toLocaleString()}
                        </div>
                    </div>
                </div>

                {payments.length === 0 ? (
                    <div className="text-muted text-center p-4">Không có dữ liệu thanh toán.</div>
                ) : (
                    <div style={{ width: "100%", height: 320 }}>
                        <ResponsiveContainer>
                            <BarChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis dataKey="monthLabel" />
                                <YAxis
                                    tickFormatter={(value) =>
                                        value >= 1000000
                                            ? `${(value / 1000000).toFixed(1)}M`
                                            : value.toLocaleString()
                                    }
                                />
                                <Tooltip
                                    formatter={(value) => [`${value.toLocaleString()}`, "Doanh thu"]}
                                    labelFormatter={(label) => `Tháng ${label.replace('T', '')}`}
                                />
                                <Bar dataKey="amount" fill="#28a745" radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
};

export default PaymentsChart;