import { useEffect, useState } from "react";
import { Card, Col, Row, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom";
import { authApis, endpoints } from "../configs.js";
import "./admin.css";
import PaymentsChart from "./PaymentsChart.js";

const StatCard = ({ title, value, loading, foot }) => (
    <Card className="shadow-sm border-0 h-100">
        <Card.Body>
            <div className="text-muted small">{title}</div>
            <div className="display-6 fw-semibold text-success">
                {loading ? <Spinner animation="border" size="sm" /> : value}
            </div>
            {foot && <div className="mt-2 small">{foot}</div>}
        </Card.Body>
    </Card>
);

const AdminDashboard = () => {
    const [loading, setLoading] = useState(true);
    const [counts, setCounts] = useState({
        users: "—",
        lessons: "—",
        categories: "—",
        vocabularies: "—",
        payments: "—",
    });
    const [paymentsData, setPaymentsData] = useState([]);

    useEffect(() => {
        const api = authApis();
        (async () => {
            setLoading(true);
            const [users, lessons, categories, vocabularies, payments] = await Promise.all([
                api.get(endpoints.users),
                api.get(endpoints.lessons),
                api.get(endpoints.categories),
                api.get(endpoints.vocabularies),
                api.get(endpoints.payments),
            ]);
            setCounts({
                users: Array.isArray(users.data) ? users.data.length : "—",
                lessons: Array.isArray(lessons.data) ? lessons.data.length : "—",
                categories: Array.isArray(categories.data) ? categories.data.length : "—",
                vocabularies: Array.isArray(vocabularies.data) ? vocabularies.data.length : "—",
                payments: Array.isArray(payments.data) ? payments.data.length : "—",
            });
            setPaymentsData(Array.isArray(payments.data) ? payments.data : []);
            setLoading(false);
        })();
    }, []);

    return (
        <>
            <h3 className="mb-2">Báo cáo thống kê</h3>

            <Row className="g-3 mb-4">
                <Col sm={6} xl={4}>
                    <StatCard title="Người dùng" value={counts.users} loading={loading} />
                </Col>
                <Col sm={6} xl={4}>
                    <StatCard
                        title="Bài học"
                        value={counts.lessons}
                        loading={loading}
                        foot={<Link to="/admin/lessons">Quản lý bài học →</Link>}
                    />
                </Col>
                <Col sm={6} xl={4}>
                    <StatCard
                        title="Danh mục"
                        value={counts.categories}
                        loading={loading}
                        foot={<Link to="/admin/categories">Quản lý danh mục →</Link>}
                    />
                </Col>
                <Col sm={6} xl={4}>
                    <StatCard title="Từ vựng (toàn hệ thống)" value={counts.vocabularies} loading={loading} />
                </Col>
                <Col sm={6} xl={4}>
                    <StatCard title="Giao dịch thanh toán" value={counts.payments} loading={loading} />
                </Col>
            </Row>
            <Row className="g-3 mb-4">
                <Col xs={12}>
                    <PaymentsChart payments={paymentsData} />
                </Col>
            </Row>

        </>
    );
};

export default AdminDashboard;
