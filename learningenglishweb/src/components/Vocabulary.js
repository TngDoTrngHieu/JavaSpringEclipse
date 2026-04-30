import { useEffect, useState } from "react";
import { Alert, Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { Navigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs.js";

const emptyForm = { word: "", meaning: "", example: "", note: "" };

const Vocabulary = () => {
    const token = cookie.load("token");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [items, setItems] = useState([]);
    const [keyword, setKeyword] = useState("");
    const [form, setForm] = useState(emptyForm);
    const [err, setErr] = useState("");
    const [ok, setOk] = useState("");

    const loadData = async (searchKeyword = "") => {
        const api = authApis();
        setLoading(true);
        setErr("");
        try {
            const res = searchKeyword
                ? await api.get(endpoints.vocabularySearch, { params: { keyword: searchKeyword } })
                : await api.get(endpoints.vocabularies);
            setItems(Array.isArray(res.data) ? res.data : []);
        } catch (e) {
            const d = e?.response?.data;
            setErr(typeof d === "string" ? d : (d?.error ?? d?.message ?? "Không tải được sổ từ vựng."));
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (token) loadData("");
    }, [token]);

    if (!token) return <Navigate to="/login?next=%2Fvocabulary" replace />;

    const submit = async (e) => {
        e.preventDefault();
        if (!form.word.trim()) return;
        const api = authApis();
        setSaving(true);
        setErr("");
        setOk("");
        try {
            await api.post(endpoints.vocabularies, {
                word: form.word.trim(),
                meaning: form.meaning,
                example: form.example,
                note: form.note,
            });
            setForm(emptyForm);
            setOk("Đã thêm từ mới.");
            await loadData(keyword.trim());
        } catch (errObj) {
            const d = errObj?.response?.data;
            setErr(typeof d === "string" ? d : (d?.error ?? d?.message ?? "Không thêm được từ."));
        } finally {
            setSaving(false);
        }
    };

    const remove = async (id) => {
        if (!window.confirm("Xóa từ này khỏi sổ từ vựng?")) return;
        const api = authApis();
        setErr("");
        setOk("");
        try {
            await api.delete(endpoints.vocabularyById(id));
            await loadData(keyword.trim());
        } catch (e) {
            const d = e?.response?.data;
            setErr(typeof d === "string" ? d : (d?.error ?? d?.message ?? "Không xóa được từ."));
        }
    };

    return (
        <Container className="py-4">
            <h3 className="mb-3">Sổ từ vựng cá nhân</h3>
            <Row className="g-3">
                <Col lg={5}>
                    <Card className="shadow-sm border-0">
                        <Card.Body>
                            <h5 className="mb-3">Thêm từ mới</h5>
                            <Form onSubmit={submit}>
                                <Form.Group className="mb-2">
                                    <Form.Label>Từ</Form.Label>
                                    <Form.Control
                                        value={form.word}
                                        onChange={(e) => setForm({ ...form, word: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group className="mb-2">
                                    <Form.Label>Nghĩa</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={2}
                                        value={form.meaning}
                                        onChange={(e) => setForm({ ...form, meaning: e.target.value })}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-2">
                                    <Form.Label>Ví dụ</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={2}
                                        value={form.example}
                                        onChange={(e) => setForm({ ...form, example: e.target.value })}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ghi chú</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={2}
                                        value={form.note}
                                        onChange={(e) => setForm({ ...form, note: e.target.value })}
                                    />
                                </Form.Group>
                                <Button type="submit" variant="success" disabled={saving}>
                                    {saving ? "Đang thêm..." : "Thêm từ"}
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
                <Col lg={7}>
                    <Card className="shadow-sm border-0">
                        <Card.Body>
                            <div className="d-flex gap-2 mb-3">
                                <Form.Control
                                    placeholder="Tìm từ..."
                                    value={keyword}
                                    onChange={(e) => setKeyword(e.target.value)}
                                />
                                <Button variant="outline-secondary" onClick={() => loadData(keyword.trim())}>
                                    Search
                                </Button>
                            </div>
                            {err && <Alert variant="danger">{err}</Alert>}
                            {ok && <Alert variant="success">{ok}</Alert>}
                            {loading ? (
                                <Spinner animation="border" />
                            ) : (
                                <Table responsive striped bordered hover size="sm">
                                    <thead>
                                        <tr>
                                            <th>Word</th>
                                            <th>Meaning</th>
                                            <th>Example</th>
                                            <th style={{ width: 90 }}></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {items.map((v) => (
                                            <tr key={v.id}>
                                                <td>{v.word}</td>
                                                <td>{v.meaning}</td>
                                                <td>{v.example}</td>
                                                <td>{v.note}</td>
                                                <td>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-danger"
                                                        onClick={() => remove(v.id)}
                                                    >
                                                        Xóa
                                                    </Button>
                                                </td>
                                            </tr>
                                        ))}
                                        {items.length === 0 && (
                                            <tr>
                                                <td colSpan={4} className="text-center text-muted">
                                                    Chưa có từ vựng.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Vocabulary;
