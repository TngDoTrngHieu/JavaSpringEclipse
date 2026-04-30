import React, { useState, useEffect } from "react";
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { useSearchParams, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "./configs.js";

const ResetPasswordPage = () => {
    const [q] = useSearchParams();
    const token = q.get("token") || "";
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [ok, setOk] = useState("");
    const nav = useNavigate();

    useEffect(() => {
        if (!token) setErr("Token không tồn tại hoặc đã bị thay đổi.");
    }, [token]);

    const submit = async (e) => {
        e.preventDefault();
        setErr(""); setOk("");
        if (!token) { setErr("Token không tồn tại."); return; }
        if (!newPassword || !confirmPassword) { setErr("Vui lòng điền đầy đủ"); return; }
        if (newPassword !== confirmPassword) { setErr("Mật khẩu xác nhận không khớp"); return; }
        if (newPassword.length < 6) { setErr("Mật khẩu phải có ít nhất 6 ký tự"); return; }
        setLoading(true);
        try {
            await Apis.post(endpoints.resetPassword, { token, newPassword });
            setOk("Đổi mật khẩu thành công. Chuyển tới trang đăng nhập...");
            setTimeout(() => nav("/login"), 1500);
        } catch (e) {
            setErr(e?.response?.data?.error || "Lỗi khi đổi mật khẩu");
        } finally { setLoading(false); }
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={6}>
                    <Card>
                        <Card.Body>
                            <h4 className="mb-3">Đặt lại mật khẩu</h4>
                            {err && <Alert variant="danger">{err}</Alert>}
                            {ok && <Alert variant="success">{ok}</Alert>}
                            <Form onSubmit={submit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Mật khẩu mới</Form.Label>
                                    <Form.Control type="password" value={newPassword} onChange={e => setNewPassword(e.target.value)} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Xác nhận mật khẩu mới</Form.Label>
                                    <Form.Control type="password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} />
                                </Form.Group>
                                <div className="d-grid gap-2">
                                    <Button type="submit" variant="success" disabled={loading}>{loading ? <><Spinner size="sm" className="me-2" />Đang lưu...</> : "Lưu mật khẩu"}</Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ResetPasswordPage;
