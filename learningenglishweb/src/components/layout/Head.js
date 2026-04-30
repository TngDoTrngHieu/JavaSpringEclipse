import { useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs.js";

const displayUserName = (u) => {
    if (!u) return "";
    const full = [u.firstname, u.lastname].filter(Boolean).join(" ").trim();
    return full || u.username || u.email || "";
};

const Head = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [me, setMe] = useState(null);
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        const token = cookie.load("token");
        const raw = localStorage.getItem("currentUser");
        if (token && raw) {
            try { setMe(JSON.parse(raw)); } catch { setMe(null); }
        } else {
            setMe(null);
        }
    }, [location.pathname]);

    useEffect(() => {
        const token = cookie.load("token");
        if (!token) {
            setCategories([]);
            return;
        }
        const api = authApis();
        (async () => {
            try {
                const { data } = await api.get(endpoints.categories);
                setCategories(Array.isArray(data) ? data : []);
            } catch {
                setCategories([]);
            }
        })();
    }, [location.pathname]);

    const logout = () => {
        cookie.remove("token", { path: "/" });
        localStorage.removeItem("currentUser");
        setMe(null);
        navigate("/");
    };

    // Style tùy chỉnh nhanh
    const styles = {
        navBar: {
            backgroundColor: "#ffffff",
            borderBottom: "2px solid #e9ecef",
            padding: "0.8rem 0",
            boxShadow: "0 2px 10px rgba(0,0,0,0.05)"
        },
        logo: {
            fontWeight: "800",
            letterSpacing: "-0.5px",
            color: "#198754",
            fontSize: "1.5rem"
        },
        navLink: {
            fontWeight: "500",
            color: "#495057",
            padding: "0.5rem 1rem",
            transition: "all 0.2s"
        },
        adminLink: {
            color: "#dc3545",
            fontWeight: "600",
            borderLeft: "1px solid #dee2e6",
            marginLeft: "10px",
            paddingLeft: "20px"
        },
        btnRegister: {
            borderRadius: "8px",
            fontWeight: "600",
            padding: "8px 24px",
            backgroundColor: "#198754",
            border: "none",
            marginLeft: "12px"
        }
    };

    return (
        <Navbar expand="lg" style={styles.navBar} sticky="top">
            <Container>
                <Navbar.Brand as={Link} to="/" style={styles.logo}>
                    LEARNING <span style={{ color: "#212529" }}>ENGLISH</span>
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="main-nav" border="0" />

                <Navbar.Collapse id="main-nav">
                    <Nav className="me-auto align-items-center">
                        <Nav.Link as={Link} to="/" style={styles.navLink}>
                            Trang chủ
                        </Nav.Link>

                        {me && (
                            <>
                                <Nav.Link as={Link} to="/dashboard" style={styles.navLink}>
                                    Dashboard
                                </Nav.Link>
                                <NavDropdown title="Kỹ năng" id="skill-nav-dropdown">
                                    <NavDropdown.Item as={Link} to="/dashboard?skill=READING">Reading</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/dashboard?skill=LISTENING">Listening</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/dashboard?skill=WRITING">Writing</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/dashboard?skill=SPEAKING">Speaking</NavDropdown.Item>
                                </NavDropdown>
                                <NavDropdown title="Danh mục" id="category-nav-dropdown">
                                    <NavDropdown.Item as={Link} to="/dashboard">Tất cả</NavDropdown.Item>
                                    {categories.map((c) => (
                                        <NavDropdown.Item
                                            key={c.id}
                                            as={Link}
                                            to={`/dashboard?categoryId=${c.id}`}
                                        >
                                            {c.name}
                                        </NavDropdown.Item>
                                    ))}
                                </NavDropdown>
                                <Nav.Link href="/progress">Tiến độ</Nav.Link>
                                <Nav.Link as={Link} to="/chat" style={styles.navLink}>
                                    Chat
                                </Nav.Link>
                                <Nav.Link as={Link} to="/ai-generate-quiz" >
                                    Tạo câu hỏi
                                </Nav.Link>
                            </>
                        )}

                        {me?.role === "ADMIN" && (
                            <Nav.Link as={Link} to="/admin" style={{ ...styles.navLink, ...styles.adminLink }}>
                                QUẢN TRỊ
                            </Nav.Link>
                        )}
                    </Nav>

                    <Nav className="align-items-center">
                        {me ? (
                            <>
                                <div className="text-end me-3 d-none d-lg-block">
                                    <div style={{ fontSize: "0.75rem", color: "#6c757d", lineHeight: "1" }}>Xin chào,</div>
                                    <div style={{ fontWeight: "700", color: "#212529" }}>{displayUserName(me)}</div>
                                </div>
                                <NavDropdown
                                    title={<span style={{ fontWeight: "600", color: "#198754" }}>Tài khoản</span>}
                                    align="end"
                                    id="account-dd"
                                >
                                    <NavDropdown.Header className="d-lg-none">
                                        {displayUserName(me)}
                                    </NavDropdown.Header>
                                    <NavDropdown.Item as={Link} to="/profile">Thông tin cá nhân</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/vocabulary">Sổ từ vựng</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/upgrade-vip">Nâng cấp VIP</NavDropdown.Item>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item onClick={logout} className="text-danger">
                                        Đăng xuất
                                    </NavDropdown.Item>
                                </NavDropdown>
                            </>
                        ) : (
                            <div className="d-flex align-items-center">
                                <Nav.Link as={Link} to="/login" style={{ ...styles.navLink, marginRight: "5px" }}>
                                    Đăng nhập
                                </Nav.Link>
                                <Button as={Link} to="/register" style={styles.btnRegister}>
                                    Đăng ký
                                </Button>
                            </div>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Head;