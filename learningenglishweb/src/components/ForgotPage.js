import React, { useState } from "react";
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import Apis, { endpoints } from "./configs.js";

const ForgotPage = () => {
    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [err, setErr] = useState("");

    const send = async () => {
        setErr(""); setMsg("");
        if (!email) { setErr("Vui lòng nhập email"); return; }
        setLoading(true);
        try {
            const res = await Apis.post(endpoints.forgotPassword, { email });
            setMsg(res?.data?.message);
        } catch (e) {
            setErr(e?.response?.data?.error);
        } finally { setLoading(false); }
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={6}>
                    <Card>
                        <Card.Body>
                            <h4 className="mb-3">Quên mật khẩu</h4>
                            {err && <Alert variant="danger">{err}</Alert>}
                            {msg && <Alert variant="success">{msg}</Alert>}
                            <Form onSubmit={(e) => { e.preventDefault(); send(); }}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Địa chỉ email</Form.Label>
                                    <Form.Control value={email} onChange={e => setEmail(e.target.value)} />
                                </Form.Group>
                                <div className="d-grid gap-2">
                                    <Button type="submit" variant="primary" disabled={loading}>{loading ? <><Spinner size="sm" className="me-2" />Đang gửi...</> : "Gửi link khôi phục"}</Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ForgotPage;
