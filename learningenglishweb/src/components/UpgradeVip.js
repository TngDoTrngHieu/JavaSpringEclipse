import { useEffect, useMemo, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Row, Spinner } from "react-bootstrap";
import { Navigate, useNavigate, useSearchParams } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs";

const formatVnd = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(value || 0));

const UpgradeVip = () => {
  const token = cookie.load("token");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [me, setMe] = useState(null);
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);
  const [err, setErr] = useState("");



  useEffect(() => {
    const success = searchParams.get("success");
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");

    if (!orderId) return;

    if (success === "true") {
      navigate(`/thankyou?orderId=${encodeURIComponent(orderId)}&amount=${encodeURIComponent(amount || "")}`, {
        replace: true,
      });
    } else {
      setErr("Thanh toán thất bại");
    }
  }, [navigate, searchParams]);

  const loadProfile = async () => {
    try {
      setLoading(true);
      setErr("");
      const profileRes = await authApis().get(endpoints.profile);
      setMe(profileRes.data);
    } catch (e) {
      setErr("Không thể tải dữ liệu người dùng. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!token) return;
    const loadData = async () => {
      try {
        setLoading(true);
        setErr("");
        const packageRes = await authApis().get(endpoints.vipPackages);
        setPackages(Array.isArray(packageRes.data) ? packageRes.data : []);
      } catch {
        setErr("Không thể tải dữ liệu gói VIP. Vui lòng thử lại.");
      } finally {
        setLoading(false);
      }
    };
    loadProfile();
    loadData();
  }, [token]);

  const activePackages = useMemo(
    () => packages.filter((pkg) => pkg?.isActive === true || pkg?.isActive === 1),
    [packages]
  );

  const startMomoPayment = async (pkg) => {
    if (!me?.id) { setErr("Không lấy được thông tin người dùng."); return; }
    try {
      setErr("");
      setProcessingId(pkg.id);
      const res = await authApis().post(endpoints.paymentProcess, {
        userId: me.id,
        vipPackageId: pkg.id,
      });
      const payUrl = res?.data?.payUrl;
      if (!payUrl) throw new Error("Hệ thống chưa trả về link thanh toán.");
      window.location.href = payUrl;
    } catch (e) {
      setErr(e?.response?.data?.error || "Không thể khởi tạo thanh toán MoMo.");
    } finally {
      setProcessingId(null);
    }
  };

  if (!token) return <Navigate to="/login?next=/upgrade-vip" replace />;

  return (
    <div style={{ background: "#f8f9fa", minHeight: "100vh", padding: "40px 0" }}>
      <Container>
        <div className="mb-4 text-center">
          <h2 className="fw-bold mb-2">Nâng cấp tài khoản VIP</h2>
          <p className="text-muted mb-0">Chọn gói phù hợp và thanh toán qua MoMo để kích hoạt ngay.</p>
        </div>

        {err && <Alert variant="danger">{err}</Alert>}

        {loading ? (
          <div className="text-center py-5">
            <Spinner animation="border" variant="success" />
          </div>
        ) : activePackages.length === 0 ? (
          <Alert variant="warning">Hiện chưa có gói VIP khả dụng.</Alert>
        ) : (
          <Row className="g-4">
            {activePackages.map((pkg) => (
              <Col md={6} lg={4} key={pkg.id}>
                <Card className="h-100 shadow-sm border-0">
                  <Card.Body className="d-flex flex-column">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <h5 className="fw-bold mb-0">{pkg.name}</h5>
                      <Badge bg="success">VIP</Badge>
                    </div>
                    <div className="text-muted mb-3" style={{ minHeight: 48 }}>
                      {pkg.description || "Gói học nâng cao dành cho thành viên VIP."}
                    </div>
                    <div className="mb-2">
                      <span className="fw-semibold">Thời hạn:</span> {pkg.months} tháng
                    </div>
                    <div className="mb-4 fs-5 fw-bold text-success">{formatVnd(pkg.price)}</div>
                    <Button
                      variant="success"
                      className="mt-auto"
                      disabled={processingId === pkg.id}
                      onClick={() => startMomoPayment(pkg)}
                    >
                      {processingId === pkg.id ? (
                        <><Spinner size="sm" className="me-2" />Đang tạo giao dịch...</>
                      ) : "Thanh toán qua MoMo"}
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Container>
    </div>
  );
};

export default UpgradeVip;