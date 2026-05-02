import { useRef, useState } from "react";
import { Alert, Button, Form, Container, Row, Col, Card, InputGroup } from "react-bootstrap";
import MySpinner from "./layout/MySpiner";
import Apis, { endpoints } from "./configs.js";
import { useNavigate, Link } from "react-router-dom";

const Register = () => {
    const avatar = useRef();
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState();
    const nav = useNavigate();
    const [showPwd, setShowPwd] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);


    const styles = {
        pageWrapper: {
            background: "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
            minHeight: "100vh",
            padding: "50px 0",
            display: "flex",
            alignItems: "center"
        },
        card: {
            borderRadius: "24px",
            border: "none",
            boxShadow: "0 20px 40px rgba(0,0,0,0.08)",
            overflow: "hidden"
        },
        input: {
            borderRadius: "12px",
            padding: "12px 16px",
            fontSize: "0.95rem",
            border: "1px solid #dee2e6",
            backgroundColor: "#fdfdfd",
            transition: "all 0.2s ease"
        },
        label: {
            fontSize: "0.8rem",
            fontWeight: "700",
            textTransform: "uppercase",
            letterSpacing: "0.5px",
            color: "#6c757d",
            marginBottom: "6px"
        },
        btnSubmit: {
            borderRadius: "12px",
            padding: "14px",
            fontWeight: "700",
            fontSize: "1.1rem",
            backgroundColor: "#198754",
            border: "none",
            marginTop: "1.5rem",
            boxShadow: "0 4px 12px rgba(25, 135, 84, 0.2)"
        }
    };


    const validate = () => {
        const requiredFields = ['firstName', 'lastName', 'phone', 'email', 'username', 'password', 'confirm'];
        for (let field of requiredFields) {
            if (!user[field]?.trim()) {
                setErr("Vui lòng điền đầy đủ tất cả các trường!");
                return false;
            }
        }
        if (user.password !== user.confirm) {
            setErr("Mật khẩu xác nhận không khớp!");
            return false;
        }
        if (user.password.length < 6) {
            setErr("Mật khẩu phải từ 6 ký tự trở lên!");
            return false;
        }
        return true;
    };

    const register = async (e) => {
        e.preventDefault();
        if (validate()) {
            try {
                setLoading(true);
                setErr(null);
                const formData = new FormData();
                formData.append("firstname", user.firstName.trim());
                formData.append("lastname", user.lastName.trim());
                formData.append("email", user.email.trim());
                formData.append("username", user.username.trim());
                formData.append("password", user.password);
                formData.append("phone", user.phone.replace(/\s/g, ""));
                if (avatar.current?.files?.[0]) formData.append("avatar", avatar.current.files[0]);

                const res = await Apis.post(endpoints.register, formData);
                if (res.status >= 200 && res.status < 300) nav("/login");
            } catch (ex) {
                setErr("Đăng ký không thành công. Vui lòng kiểm tra lại thông tin!");
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div style={styles.pageWrapper}>
            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} md={10} lg={8} xl={7}>
                        <Card style={styles.card}>
                            <div style={{ height: "8px", backgroundColor: "#198754" }}></div>
                            <Card.Body className="p-4 p-md-5">
                                <div className="text-center mb-5">
                                    <h2 className="fw-black text-dark" style={{ letterSpacing: "-1px" }}>
                                        TẠO TÀI KHOẢN
                                    </h2>
                                    <p className="text-muted small">Tham gia cùng hàng nghìn học viên tiếng Anh mỗi ngày</p>
                                    <div className="mx-auto mt-2" style={{ width: "50px", height: "3px", backgroundColor: "#198754", borderRadius: "2px" }}></div>
                                </div>

                                {err && (
                                    <Alert variant="danger" className="text-center small border-0 rounded-3 mb-4">
                                        {err}
                                    </Alert>
                                )}

                                <Form onSubmit={register}>
                                    <Row>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="firstName">
                                                <Form.Label style={styles.label}>Tên</Form.Label>
                                                <Form.Control style={styles.input} value={user.firstName || ""} onChange={e => setUser({ ...user, firstName: e.target.value })} type="text" placeholder="Ví dụ: Anh" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="lastName">
                                                <Form.Label style={styles.label}>Họ và tên lót</Form.Label>
                                                <Form.Control style={styles.input} value={user.lastName || ""} onChange={e => setUser({ ...user, lastName: e.target.value })} type="text" placeholder="Ví dụ: Nguyễn Văn" />
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <Row>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="phone">
                                                <Form.Label style={styles.label}>Số điện thoại</Form.Label>
                                                <Form.Control style={styles.input} value={user.phone || ""} onChange={e => setUser({ ...user, phone: e.target.value })} type="tel" placeholder="09xxxxxxx" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="email">
                                                <Form.Label style={styles.label}>Email cá nhân</Form.Label>
                                                <Form.Control style={styles.input} value={user.email || ""} onChange={e => setUser({ ...user, email: e.target.value })} type="email" placeholder="name@example.com" />
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <Form.Group className="mb-4" controlId="username">
                                        <Form.Label style={styles.label}>Tên đăng nhập</Form.Label>
                                        <Form.Control style={styles.input} value={user.username || ""} onChange={e => setUser({ ...user, username: e.target.value })} type="text" placeholder="Ít nhất 3 ký tự" />
                                    </Form.Group>

                                    <Row>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="password">
                                                <Form.Label style={styles.label}>Mật khẩu</Form.Label>
                                                <InputGroup>
                                                    <Form.Control style={{ ...styles.input, borderTopRightRadius: 0, borderBottomRightRadius: 0 }} value={user.password || ""} onChange={e => setUser({ ...user, password: e.target.value })} type={showPwd ? "text" : "password"} placeholder="••••••••" />
                                                    <Button variant="outline-light" style={{ border: "1px solid #dee2e6", color: "#6c757d", fontSize: "0.8rem" }} onClick={() => setShowPwd(!showPwd)}>
                                                        {showPwd ? "ẨN" : "HIỆN"}
                                                    </Button>
                                                </InputGroup>
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group className="mb-4" controlId="confirm">
                                                <Form.Label style={styles.label}>Xác nhận lại</Form.Label>
                                                <InputGroup>
                                                    <Form.Control style={{ ...styles.input, borderTopRightRadius: 0, borderBottomRightRadius: 0 }} value={user.confirm || ""} onChange={e => setUser({ ...user, confirm: e.target.value })} type={showConfirm ? "text" : "password"} placeholder="••••••••" />
                                                    <Button variant="outline-light" style={{ border: "1px solid #dee2e6", color: "#6c757d", fontSize: "0.8rem" }} onClick={() => setShowConfirm(!showConfirm)}>
                                                        {showConfirm ? "ẨN" : "HIỆN"}
                                                    </Button>
                                                </InputGroup>
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <Form.Group className="mb-4" controlId="avatar">
                                        <Form.Label style={styles.label}>Ảnh đại diện (Tùy chọn)</Form.Label>
                                        <Form.Control style={{ ...styles.input, paddingTop: "8px" }} type="file" ref={avatar} accept="image/*" />
                                        <Form.Text className="text-muted italic small">Định dạng hỗ trợ: JPG, PNG, WEBP</Form.Text>
                                    </Form.Group>

                                    {loading ? (
                                        <div className="text-center py-3"><MySpinner /></div>
                                    ) : (
                                        <Button type="submit" style={styles.btnSubmit} className="w-100">
                                            HOÀN TẤT ĐĂNG KÝ
                                        </Button>
                                    )}

                                    <div className="text-center mt-4 pt-2 border-top">
                                        <span className="text-muted small">Bạn đã có tài khoản? </span>
                                        <Link to="/login" className="small fw-bold text-success text-decoration-none">
                                            Đăng nhập ngay
                                        </Link>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default Register;