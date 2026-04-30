import { Alert, Button, Form, Container, Row, Col, Card, InputGroup } from "react-bootstrap";
import MySpinner from "./layout/MySpiner";
import { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Apis, { authApis, endpoints } from "./configs.js";
import cookie from "react-cookies";
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
const Login = () => {
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState();
    const nav = useNavigate();
    const [q] = useSearchParams();
    const [showPwd, setShowPwd] = useState(false);

    const [googleClientId, setGoogleClientId] = useState("");
    const [clientLoading, setClientLoading] = useState(true);

    useEffect(() => {
        let mounted = true;
        const loadClientId = async () => {
            try {
                const res = await Apis.get(endpoints.googleClientId);
                const id = res?.data?.client_id ?? res?.data?.clientId ?? "";
                if (mounted) setGoogleClientId(id);
            } catch (err) {
                console.error("Failed to load Google client id", err);
            } finally {
                if (mounted) setClientLoading(false);
            }
        };
        loadClientId();
        return () => { mounted = false; };
    }, []);

    const handleGoogleCredential = async (credential) => {
        try {
            const res = await Apis.post(endpoints.loginGoogle, { token: credential });
            const token = res.data.token;
            cookie.save("token", token, { path: "/", maxAge: 86400, sameSite: "lax", secure: false });
            const u = await authApis().get(endpoints.profile);
            // keep consistent with username/password login flow
            localStorage.setItem("currentUser", JSON.stringify(u.data));
            const next = q.get("next");
            const role = u.data?.role;
            if (role === "ADMIN") {
                nav(next && next.startsWith("/admin") ? next : "/admin");
            } else {
                nav(next?.startsWith("/admin") ? "/" : (next ?? "/"));
            }
        } catch (e) {
            const serverMsg = e?.response?.data?.error ?? e?.response?.data?.message ?? e?.message ?? "";
            setErr(serverMsg ? `Lỗi đăng nhập Google: ${serverMsg}` : "Đăng nhập Google thất bại");
        }
    };
    const styles = {
        pageWrapper: {
            background: "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
            minHeight: "calc(100vh - 160px)", // Trừ chiều cao header/footer
            display: "flex",
            alignItems: "center",
            padding: "40px 0"
        },
        card: {
            borderRadius: "20px",
            border: "none",
            boxShadow: "0 15px 35px rgba(0,0,0,0.1)",
            overflow: "hidden"
        },
        input: {
            borderRadius: "10px",
            padding: "12px 15px",
            fontSize: "1rem",
            border: "1px solid #dee2e6",
            backgroundColor: "#fdfdfd"
        },
        btnSubmit: {
            borderRadius: "10px",
            padding: "12px",
            fontWeight: "700",
            fontSize: "1.1rem",
            backgroundColor: "#198754",
            border: "none",
            transition: "all 0.3s ease",
            marginTop: "1rem"
        },
        toggleBtn: {
            borderTopRightRadius: "10px",
            borderBottomRightRadius: "10px",
            fontSize: "0.85rem",
            fontWeight: "600",
            textTransform: "uppercase"
        }
    };

    const validate = () => {
        if (!user.username?.trim() || !user.password?.trim()) {
            setErr("Vui lòng nhập đầy đủ thông tin.");
            return false;
        }
        return true;
    };

    const login = async (e) => {
        e.preventDefault();
        if (validate()) {
            try {
                setLoading(true);
                setErr(undefined);
                let res = await Apis.post(endpoints['login'], { ...user });
                cookie.save('token', res.data.token, { path: "/", maxAge: 86400, sameSite: "lax", secure: false });

                const u = await authApis().get(endpoints['profile']);
                localStorage.setItem("currentUser", JSON.stringify(u.data));

                const next = q.get("next");
                const role = u.data?.role;
                if (role === "ADMIN") {
                    nav(next && next.startsWith("/admin") ? next : "/admin");
                } else {
                    nav(next?.startsWith("/admin") ? "/" : (next ?? "/"));
                }
            } catch (e) {
                setErr("Tên đăng nhập hoặc mật khẩu không chính xác.");
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div style={styles.pageWrapper}>
            <Container>
                <Row className="justify-content-center">
                    <Col xs={11} sm={9} md={7} lg={5} xl={4}>
                        <Card style={styles.card}>
                            {/* Thanh trang trí trên đầu card */}
                            <div style={{ height: "6px", backgroundColor: "#198754" }}></div>

                            <Card.Body className="p-4 p-md-5">
                                <div className="text-center mb-5">
                                    <h2 className="fw-black" style={{ color: "#212529", letterSpacing: "-1px" }}>
                                        ĐĂNG NHẬP
                                    </h2>
                                    <div className="mx-auto mt-2" style={{ width: "40px", height: "3px", backgroundColor: "#198754", borderRadius: "2px" }}></div>
                                </div>

                                {err && (
                                    <Alert variant="danger" className="py-2 text-center small rounded-3 border-0" style={{ backgroundColor: "#fff5f5", color: "#e03131" }}>
                                        {err}
                                    </Alert>
                                )}

                                <Form onSubmit={login}>
                                    <Form.Group className="mb-4" controlId="username">
                                        <Form.Label className="small fw-bold text-uppercase text-muted">Tên đăng nhập</Form.Label>
                                        <Form.Control
                                            style={styles.input}
                                            value={user.username || ""}
                                            onChange={e => setUser({ ...user, username: e.target.value })}
                                            type="text"
                                            placeholder="Tên tài khoản của bạn"
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4" controlId="password">
                                        <div className="d-flex justify-content-between">
                                            <Form.Label className="small fw-bold text-uppercase text-muted">Mật khẩu</Form.Label>
                                            <Link to="/forgot" className="small text-decoration-none text-success">Quên mật khẩu?</Link>
                                        </div>
                                        <InputGroup>
                                            <Form.Control
                                                style={{ ...styles.input, borderTopRightRadius: 0, borderBottomRightRadius: 0 }}
                                                value={user.password || ""}
                                                onChange={e => setUser({ ...user, password: e.target.value })}
                                                type={showPwd ? "text" : "password"}
                                                placeholder="••••••••"
                                            />
                                            <Button
                                                variant="outline-light"
                                                style={{ ...styles.toggleBtn, border: "1px solid #dee2e6", color: "#6c757d" }}
                                                onClick={() => setShowPwd(!showPwd)}
                                            >
                                                {showPwd ? "Ẩn" : "Hiện"}
                                            </Button>
                                        </InputGroup>
                                    </Form.Group>

                                    {loading ? (
                                        <div className="text-center py-3"><MySpinner /></div>
                                    ) : (
                                        <Button type="submit" style={styles.btnSubmit} className="w-100 shadow-sm">
                                            VÀO HỆ THỐNG
                                        </Button>
                                    )}
                                    <div className="text-center my-4 position-relative">
                                        <hr />
                                        <span className="position-absolute top-50 start-50 translate-middle bg-white px-3 small text-muted">
                                            HOẶC
                                        </span>
                                    </div>

                                    <div className="d-flex justify-content-center">
                                        {clientLoading ? (
                                            <div className="py-2"><MySpinner /></div>
                                        ) : googleClientId ? (
                                            <GoogleOAuthProvider clientId={googleClientId}>
                                                <GoogleLogin
                                                    onSuccess={credentialResponse => {
                                                        handleGoogleCredential(credentialResponse.credential);
                                                    }}
                                                    onError={() => setErr("Đăng nhập Google thất bại")}
                                                    theme="filled_blue"
                                                    shape="pill"
                                                    text="signin_with"
                                                />
                                            </GoogleOAuthProvider>
                                        ) : (
                                            <div className="small text-muted">Google login hiện không khả dụng</div>
                                        )}
                                    </div>

                                    <div className="text-center mt-4">
                                        <span className="text-muted small">Bạn là người mới? </span>
                                        <Link to="/register" className="small fw-bold text-success text-decoration-none">
                                            Tạo tài khoản ngay
                                        </Link>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>

                        {/* Footer phụ dưới Card */}
                        <div className="text-center mt-4 small text-muted">
                            &copy; {new Date().getFullYear()} English Learning Platform
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default Login;