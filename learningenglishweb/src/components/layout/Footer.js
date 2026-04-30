import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Footer = () => {
    const styles = {
        footer: {
            backgroundColor: "#f8f9fa",
            borderTop: "1px solid #dee2e6",
            padding: "3rem 0 1.5rem 0",
            marginTop: "3rem",
            color: "#495057"
        },
        brand: {
            fontWeight: "800",
            color: "#198754",
            fontSize: "1.25rem",
            textDecoration: "none",
            display: "block",
            marginBottom: "1rem"
        },
        title: {
            fontSize: "1rem",
            fontWeight: "700",
            color: "#212529",
            marginBottom: "1.2rem",
            textTransform: "uppercase",
            letterSpacing: "0.5px"
        },
        link: {
            color: "#6c757d",
            textDecoration: "none",
            display: "block",
            marginBottom: "0.7rem",
            fontSize: "0.95rem",
            transition: "color 0.2s"
        },
        bottomBar: {
            borderTop: "1px solid #dee2e6",
            marginTop: "2.5rem",
            paddingTop: "1.5rem",
            fontSize: "0.85rem",
            color: "#adb5bd",
            textAlign: "center"
        }
    };

    return (
        <footer style={styles.footer}>
            <Container>
                <Row>
                    {/* Cột 1: Giới thiệu ngắn */}
                    <Col lg={4} md={12} className="mb-4 mb-lg-0">
                        <Link to="/" style={styles.brand}>
                            LEARNING ENGLISH
                        </Link>
                        <p style={{ lineHeight: "1.6", fontSize: "0.9rem" }}>
                            Nền tảng luyện thi IELTS trực tuyến hiệu quả, giúp bạn 
                            chinh phục mục tiêu ngôn ngữ thông qua các bài thực hành 
                            Reading, Listening và hỗ trợ từ AI.
                        </p>
                    </Col>

                    {/* Cột 2: Danh mục học tập */}
                    <Col lg={2} md={4} className="mb-4 mb-md-0">
                        <h6 style={styles.title}>Học tập</h6>
                        <Link to="/" style={styles.link}>Reading</Link>
                        <Link to="/" style={styles.link}>Listening</Link>
                        <Link to="/" style={styles.link}>Writing (AI)</Link>
                        <Link to="/" style={styles.link}>Speaking (AI)</Link>
                    </Col>

                    {/* Cột 3: Hỗ trợ */}
                    <Col lg={3} md={4} className="mb-4 mb-md-0">
                        <h6 style={styles.title}>Thông tin</h6>
                        <Link to="/" style={styles.link}>Hướng dẫn sử dụng</Link>
                        <Link to="/" style={styles.link}>Chính sách bảo mật</Link>
                        <Link to="/" style={styles.link}>Điều khoản dịch vụ</Link>
                        <Link to="/" style={styles.link}>Liên hệ hỗ trợ</Link>
                    </Col>

                    {/* Cột 4: Liên hệ */}
                    <Col lg={3} md={4}>
                        <h6 style={styles.title}>Liên hệ</h6>
                        <div style={styles.link}>Email: contact@learningenglish.com</div>
                        <div style={styles.link}>Hotline: 0123 456 789</div>
                        <div style={styles.link}>Địa chỉ: TP. Hồ Chí Minh, Việt Nam</div>
                    </Col>
                </Row>

                {/* Dòng bản quyền dưới cùng */}
                <div style={styles.bottomBar}>
                    &copy; {new Date().getFullYear()} Learning English Project. 
                    All rights reserved. Designed for Graduation Thesis.
                </div>
            </Container>
        </footer>
    );
};

export default Footer;