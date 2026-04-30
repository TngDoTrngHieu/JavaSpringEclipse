import { useEffect, useRef, useState } from "react";
import { Alert, Button, Card, Col, Container, Form, Row, Spinner, Image } from "react-bootstrap";
import { Navigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs.js";

const Profile = () => {
    const token = cookie.load("token");
    const [user, setUser] = useState(null);
    const [form, setForm] = useState({ firstname: "", lastname: "", username: "", email: "" });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");
    const [ok, setOk] = useState("");
    const avatarInput = useRef(null);
    const [pwdForm, setPwdForm] = useState({ currentPassword: "", newPassword: "", confirmPassword: "" });
    const [changingPwd, setChangingPwd] = useState(false);
    const [pwdErr, setPwdErr] = useState("");
    const [pwdOk, setPwdOk] = useState("");

    // Style đồng bộ
    const styles = {
        pageWrapper: {
            background: "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
            minHeight: "100vh",
            padding: "60px 0"
        },
        card: {
            borderRadius: "20px",
            border: "none",
            boxShadow: "0 10px 30px rgba(0,0,0,0.05)",
            overflow: "hidden"
        },
        avatarCircle: {
            width: "120px",
            height: "120px",
            objectFit: "cover",
            borderRadius: "50%",
            border: "4px solid #fff",
            boxShadow: "0 4px 15px rgba(0,0,0,0.1)",
            marginBottom: "15px"
        },
        input: {
            borderRadius: "10px",
            padding: "12px",
            border: "1px solid #eee",
            backgroundColor: "#fcfcfc"
        },
        label: {
            fontSize: "0.85rem",
            fontWeight: "600",
            color: "#495057",
            marginBottom: "8px"
        }
    };

    useEffect(() => {
        if (!token) return;
        const loadProfile = async () => {
            try {
                setLoading(true);
                const res = await authApis().get(endpoints.profile);
                setUser(res.data);
                setForm({
                    firstname: res.data?.firstname ?? "",
                    lastname: res.data?.lastname ?? "",
                    username: res.data?.username ?? "",
                    email: res.data?.email ?? "",
                });
            } catch (e) {
                setErr("Không thể tải thông tin cá nhân.");
            } finally {
                setLoading(false);
            }
        };
        loadProfile();
    }, [token]);

    const updateProfile = async (e) => {
        e.preventDefault();
        try {
            setSaving(true);
            setErr("");
            setOk("");
            const formData = new FormData();
            formData.append("firstname", form.firstname.trim());
            formData.append("lastname", form.lastname.trim());
            formData.append("username", form.username.trim());
            if (avatarInput.current?.files?.[0]) {
                formData.append("avatar", avatarInput.current.files[0]);
            }

            const res = await authApis().put(endpoints.updateProfile, formData);
            setUser(res.data);
            setOk("Cập nhật thông tin thành công!");
            if (avatarInput.current) avatarInput.current.value = "";
        } catch (e) {
            setErr("Cập nhật thất bại. Vui lòng thử lại.");
        } finally {
            setSaving(false);
        }
    };

    if (!token) return <Navigate to="/login?next=/profile" replace />;

    return (
        <div style={styles.pageWrapper}>
            <Container>
                <Row className="justify-content-center">
                    <Col lg={8} xl={6}>
                        <Card style={styles.card}>
                            {/* Header màu sắc đặc trưng */}
                            <div style={{ height: "6px", backgroundColor: "#198754" }}></div>

                            <Card.Body className="p-4 p-md-5">
                                <div className="text-center mb-4">
                                    <h2 className="fw-bold text-dark">Hồ sơ cá nhân</h2>
                                    <p className="text-muted">Quản lý thông tin và tài khoản của bạn</p>
                                </div>

                                {loading ? (
                                    <div className="text-center py-5">
                                        <Spinner animation="border" variant="success" />
                                        <p className="mt-3 text-muted">Đang tải dữ liệu...</p>
                                    </div>
                                ) : (
                                    <>
                                        {err && <Alert variant="danger" className="rounded-3 small">{err}</Alert>}
                                        {ok && <Alert variant="success" className="rounded-3 small">{ok}</Alert>}

                                        <Form onSubmit={updateProfile}>
                                            <div className="text-center mb-5">
                                                <div className="position-relative d-inline-block">
                                                    {user?.avatarUrl ? (
                                                        <Image src={user.avatarUrl} style={styles.avatarCircle} />
                                                    ) : (
                                                        <div style={{ ...styles.avatarCircle, backgroundColor: "#e9ecef" }} className="d-flex align-items-center justify-content-center text-muted">
                                                            No Image
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="small text-muted mt-1">@{user?.username}</div>
                                                {user?.isVip && (
                                                    <div className="text-success fw-bold mt-2 small">
                                                        🌟 VIP đến: {user.vipExpireAt ? new Date(user.vipExpireAt).toLocaleDateString() : "—"}
                                                    </div>
                                                )}
                                            </div>

                                            <Row>
                                                <Col md={6}>
                                                    <Form.Group className="mb-3">
                                                        <Form.Label style={styles.label}>Tên</Form.Label>
                                                        <Form.Control
                                                            style={styles.input}
                                                            value={form.firstname}
                                                            onChange={(e) => setForm({ ...form, firstname: e.target.value })}
                                                        />
                                                    </Form.Group>
                                                </Col>
                                                <Col md={6}>
                                                    <Form.Group className="mb-3">
                                                        <Form.Label style={styles.label}>Họ và tên lót</Form.Label>
                                                        <Form.Control
                                                            style={styles.input}
                                                            value={form.lastname}
                                                            onChange={(e) => setForm({ ...form, lastname: e.target.value })}
                                                        />
                                                    </Form.Group>
                                                </Col>
                                            </Row>

                                            <Form.Group className="mb-3">
                                                <Form.Label style={styles.label}>Tên đăng nhập (Username)</Form.Label>
                                                <Form.Control
                                                    style={styles.input}
                                                    value={form.username}
                                                    onChange={(e) => setForm({ ...form, username: e.target.value })}
                                                />
                                            </Form.Group>

                                            <Form.Group className="mb-3">
                                                <Form.Label style={styles.label}>Địa chỉ Email</Form.Label>
                                                <Form.Control
                                                    style={{ ...styles.input, backgroundColor: "#f8f9fa", cursor: "not-allowed" }}
                                                    value={form.email}
                                                    disabled
                                                />
                                                <Form.Text className="text-muted small">Email không thể thay đổi.</Form.Text>
                                            </Form.Group>

                                            <Form.Group className="mb-4">
                                                <Form.Label style={styles.label}>Thay đổi ảnh đại diện</Form.Label>
                                                <Form.Control
                                                    type="file"
                                                    accept="image/*"
                                                    ref={avatarInput}
                                                    style={styles.input}
                                                />
                                            </Form.Group>

                                            <div className="d-grid gap-2 pt-3">
                                                <Button
                                                    type="submit"
                                                    variant="success"
                                                    size="lg"
                                                    disabled={saving}
                                                    style={{ borderRadius: "12px", fontWeight: "600", padding: "12px" }}
                                                >
                                                    {saving ? <><Spinner size="sm" className="me-2" /> Đang lưu...</> : "Lưu thay đổi"}
                                                </Button>
                                            </div>
                                        </Form>

                                        <hr className="my-4" />

                                        <h5 className="mb-3">Đổi mật khẩu</h5>
                                        {pwdErr && <Alert variant="danger" className="rounded-3 small">{pwdErr}</Alert>}
                                        {pwdOk && <Alert variant="success" className="rounded-3 small">{pwdOk}</Alert>}
                                        <Form onSubmit={async (e) => {
                                            e.preventDefault();
                                            setPwdErr("");
                                            setPwdOk("");
                                            if (!pwdForm.currentPassword || !pwdForm.newPassword || !pwdForm.confirmPassword) {
                                                setPwdErr("Vui lòng điền đầy đủ các trường.");
                                                return;
                                            }
                                            if (pwdForm.newPassword !== pwdForm.confirmPassword) {
                                                setPwdErr("Mật khẩu mới và xác nhận không khớp.");
                                                return;
                                            }
                                            if (pwdForm.newPassword.length < 6) {
                                                setPwdErr("Mật khẩu mới phải có ít nhất 6 ký tự.");
                                                return;
                                            }
                                            try {
                                                setChangingPwd(true);
                                                await authApis().post(endpoints.changePassword, {
                                                    currentPassword: pwdForm.currentPassword,
                                                    newPassword: pwdForm.newPassword,
                                                });
                                                setPwdOk("Đổi mật khẩu thành công.");
                                                setPwdForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
                                            } catch (err) {
                                                setPwdErr(err?.response?.data?.message || "Đổi mật khẩu thất bại.");
                                            } finally {
                                                setChangingPwd(false);
                                            }
                                        }}>
                                            <Form.Group className="mb-3">
                                                <Form.Label style={styles.label}>Mật khẩu hiện tại</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    value={pwdForm.currentPassword}
                                                    onChange={(e) => setPwdForm({ ...pwdForm, currentPassword: e.target.value })}
                                                    style={styles.input}
                                                />
                                            </Form.Group>
                                            <Form.Group className="mb-3">
                                                <Form.Label style={styles.label}>Mật khẩu mới</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    value={pwdForm.newPassword}
                                                    onChange={(e) => setPwdForm({ ...pwdForm, newPassword: e.target.value })}
                                                    style={styles.input}
                                                />
                                            </Form.Group>
                                            <Form.Group className="mb-3">
                                                <Form.Label style={styles.label}>Xác nhận mật khẩu mới</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    value={pwdForm.confirmPassword}
                                                    onChange={(e) => setPwdForm({ ...pwdForm, confirmPassword: e.target.value })}
                                                    style={styles.input}
                                                />
                                            </Form.Group>
                                            <div className="d-grid gap-2 pt-2">
                                                <Button type="submit" variant="outline-success" disabled={changingPwd} style={{ borderRadius: "12px", fontWeight: "600", padding: "10px" }}>
                                                    {changingPwd ? <><Spinner size="sm" className="me-2" /> Đang đổi...</> : "Đổi mật khẩu"}
                                                </Button>
                                            </div>
                                        </Form>
                                    </>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Profile;