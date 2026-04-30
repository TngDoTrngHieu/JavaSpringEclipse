import { Container, Row, Col, Button, Card } from "react-bootstrap";
import { Link } from "react-router-dom";
import cookie from "react-cookies";
const Home = () => {
    const token = cookie.load("token");
    return (
        <>
            <Container className="py-5">
                <Row className="align-items-center">
                    <Col md={6}>
                        <h1 className="fw-bold display-5">
                            Học IELTS thông minh với AI
                        </h1>

                        <p className="text-muted mt-3">
                            Luyện Writing với AI chấm điểm, luyện Listening theo bài thực tế.
                        </p>

                        <div className="mt-4">
                            <Button
                                as={Link}
                                to="/dashboard"
                                size="lg"
                                variant="success"
                                className="me-2"
                            >
                                Bắt đầu học
                            </Button>

                            {!token && (
                                <Button
                                    as={Link}
                                    to="/login"
                                    size="lg"
                                    variant="outline-success"
                                >
                Đăng nhập
            </Button>
        )}
                        </div>
                    </Col>

                    <Col md={6}>
                        <img
                            src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png"
                            alt="hero"
                            style={{
                                width: "100%",
                                maxHeight: 350,
                                objectFit: "contain",
                            }}
                        />
                    </Col>
                </Row>
            </Container>

            {/* FEATURES */}
            <Container className="py-5">
                <h3 className="text-center mb-4">Tính năng nổi bật</h3>

                <Row className="g-4">
                    <Col md={4}>
                        <Card className="h-100 shadow-sm text-center p-3 border-0">
                            <h5>✍️ Writing AI</h5>
                            <p className="text-muted">
                                Chấm điểm bài viết tự động theo chuẩn IELTS
                            </p>
                        </Card>
                    </Col>

                    <Col md={4}>
                        <Card className="h-100 shadow-sm text-center p-3 border-0">
                            <h5>🎧 Listening</h5>
                            <p className="text-muted">
                                Luyện nghe với bài tập thực tế
                            </p>
                        </Card>
                    </Col>

                    <Col md={4}>
                        <Card className="h-100 shadow-sm text-center p-3 border-0">
                            <h5>📊 Progress</h5>
                            <p className="text-muted">
                                Theo dõi tiến độ học tập của bạn
                            </p>
                        </Card>
                    </Col>
                </Row>
            </Container>

 
        </>
    );
};

export default Home;