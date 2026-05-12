import { useEffect, useMemo, useState } from "react";
import { Alert, Button, Card, Container, Spinner } from "react-bootstrap";
import { Navigate, useNavigate, useSearchParams } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs";

const ThankYouVip = () => {
  const token = cookie.load("token");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const orderId = searchParams.get("orderId");
  const amount = searchParams.get("amount");

  useEffect(() => {
    const loadProfile = async () => {
      try {
        setLoading(true);
        setErr("");
        const res = await authApis().get(endpoints.profile);
        setProfile(res.data);
      } catch {
        setErr("Không thể tải thông tin tài khoản VIP.");
      } finally {
        setLoading(false);
      }
    };

    if (token) loadProfile();
  }, [token]);

  const daysLeft = useMemo(() => {
    const expireAt = profile?.vipExpireAt;
    if (!expireAt) return 0;
    const diffMs = new Date(expireAt).getTime() - Date.now();
    return Math.max(0, Math.ceil(diffMs / (1000 * 60 * 60 * 24)));
  }, [profile]);

  if (!token) return <Navigate to="/login?next=/thankyou" replace />;

  return (
    <div style={{ background: "#f8f9fa", minHeight: "100vh", padding: "40px 0" }}>
      <Container style={{ maxWidth: 560 }}>
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-4 p-md-5">
            <div className="text-center mb-4">
              <div className="fw-bold fs-3 text-success mb-2">Cảm ơn bạn đã nâng cấp VIP!</div>
              <div className="text-muted">Tài khoản của bạn đã được kích hoạt quyền thành viên VIP.</div>
            </div>

            {loading ? (
              <div className="text-center py-3">
                <Spinner animation="border" variant="success" />
              </div>
            ) : err ? (
              <Alert variant="danger">{err}</Alert>
            ) : (
              <>
                <div className="bg-light rounded-3 p-3 mb-3">
                  <div className="d-flex justify-content-between">
                    <span className="text-muted">Trạng thái</span>
                    <strong>{profile?.isVip ? "Bạn đang là user VIP" : "Chưa kích hoạt VIP"}</strong>
                  </div>
                  <div className="d-flex justify-content-between mt-2">
                    <span className="text-muted">Còn lại</span>
                    <strong>{daysLeft} ngày VIP</strong>
                  </div>
                  {profile?.vipExpireAt && (
                    <div className="d-flex justify-content-between mt-2">
                      <span className="text-muted">Hết hạn</span>
                      <strong>{new Date(profile.vipExpireAt).toLocaleDateString("vi-VN")}</strong>
                    </div>
                  )}
                </div>

                {(orderId || amount) && (
                  <div className="bg-light rounded-3 p-3 mb-3">
                    {orderId && (
                      <div className="d-flex justify-content-between">
                        <span className="text-muted">Mã giao dịch</span>
                        <code>{orderId}</code>
                      </div>
                    )}
                    {amount && (
                      <div className="d-flex justify-content-between mt-2">
                        <span className="text-muted">Số tiền</span>
                        <strong>
                          {new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(amount))}
                        </strong>
                      </div>
                    )}
                  </div>
                )}
              </>
            )}

            <div className="d-grid">
              <Button variant="success" onClick={() => navigate("/upgrade-vip")}>
                Quay lại trang nâng cấp VIP
              </Button>
            </div>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
};

export default ThankYouVip;
