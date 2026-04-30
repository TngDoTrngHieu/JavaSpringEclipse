import { Col, Container, Nav, Row } from "react-bootstrap";
import { Link, Navigate, Outlet, useLocation, useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import './admin.css';
const readUser = () => {
    const raw = localStorage.getItem("currentUser");
    if (!raw) return null;
    return JSON.parse(raw);
};

const AdminLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const token = cookie.load("token");
    const user = readUser();

    if (!token || !user) {
        return <Navigate to={`/login?next=${encodeURIComponent(location.pathname)}`} replace />;
    }
    if (user.role !== "ADMIN") {
        return <Navigate to="/" replace />;
    }

    const logout = () => {
        cookie.remove("token", { path: "/" });
        localStorage.removeItem("currentUser");
        navigate("/login");
    };

    return (
        <Container fluid className="px-0">
            <Row className="g-0" style={{ minHeight: "calc(100vh - 0px)" }}>
                <Col md={3} lg={2} className="bg-dark text-white p-3 d-flex flex-column">
                    <div className="fw-bold fs-5 mb-1">Quản trị</div>
                    <div className="small text-white-50 mb-4">Learning English · Admin</div>
                    <Nav className="flex-column gap-1">
                        <Nav.Link as={Link} to="/admin" className="text-white rounded px-2 py-2">
                            Tổng quan &amp; thống kê
                        </Nav.Link>
                        <Nav.Link as={Link} to="/admin/categories" className="text-white rounded px-2 py-2">
                            Danh mục (Category)
                        </Nav.Link>
                        <Nav.Link as={Link} to="/admin/lessons" className="text-white rounded px-2 py-2">
                            Bài học (Lesson)
                        </Nav.Link>
                    </Nav>
                    <div className="mt-auto pt-4">
                        <Nav.Link as={Link} to="/" className="text-white-50 small px-2">
                            ← Về trang học viên
                        </Nav.Link>
                        <button type="button" className="btn btn-outline-light btn-sm w-100 mt-2" onClick={logout}>
                            Đăng xuất
                        </button>
                    </div>
                </Col>
                <Col md={9} lg={10} className="bg-light min-vh-100">
                    <div className="p-4">
                        <Outlet />
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default AdminLayout;
