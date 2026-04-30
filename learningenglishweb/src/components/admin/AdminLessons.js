import { useCallback, useEffect, useState } from "react";
import { Button, Col, Form, Modal, Row, Spinner, Table } from "react-bootstrap";
import { Link } from "react-router-dom";
import { authApis, endpoints } from "../configs.js";
import "./admin.css";

const AdminLessons = () => {
    const [list, setList] = useState([]);
    const [categories, setCategories] = useState([]);
    const [lessonTypes, setLessonTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modal, setModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [form, setForm] = useState({
        title: "",
        imageUrl: "",
        content: "",
        categoryId: "",
        lessonTypeId: "",
        imageFile: null,
        preview: null,
    });

    // preview/local file handling
    const handleFileChange = (e) => {
        const file = e.target.files && e.target.files[0];
        if (!file) return;
        // revoke previous preview if any
        if (form.preview) {
            try {
                URL.revokeObjectURL(form.preview);
            } catch (e) { }
        }

        const preview = URL.createObjectURL(file);
        setForm((f) => ({ ...f, imageFile: file, preview }));
        console.log("selected file:", file);
    };

    const [saving, setSaving] = useState(false);
    const [saveError, setSaveError] = useState("");
    const [q, setQ] = useState("");

    const load = useCallback(async () => {
        const api = authApis();
        setLoading(true);
        try {
            const [l, c, t] = await Promise.all([
                api.get(endpoints.lessons),
                api.get(endpoints.categories),
                api.get(endpoints.lessonTypes),
            ]);
            setList(Array.isArray(l.data) ? l.data : []);
            setCategories(Array.isArray(c.data) ? c.data : []);
            setLessonTypes(Array.isArray(t.data) ? t.data : []);
            setForm((prev) => ({
                ...prev,
                categoryId:
                    prev.categoryId || (Array.isArray(c.data) && c.data[0] ? String(c.data[0].id) : ""),
                lessonTypeId:
                    prev.lessonTypeId || (Array.isArray(t.data) && t.data[0] ? String(t.data[0].id) : ""),
            }));
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        load();
    }, [load]);

    const openNew = () => {
        setEditing(null);
        setForm({
            title: "",
            imageUrl: "",
            content: "",
            categoryId: categories[0]?.id ? String(categories[0].id) : "",
            lessonTypeId: lessonTypes[0]?.id ? String(lessonTypes[0].id) : "",
            imageFile: null,
            preview: null,
        });
        setSaveError("");
        setModal(true);
    };

    const openEdit = async (row) => {
        const api = authApis();
        const { data } = await api.get(endpoints.lessonById(row.id));
        setEditing(data);
        setForm({
            title: data.title ?? "",
            imageUrl: data.imageUrl ?? "",
            content: data.content ?? "",
            categoryId: data.categoryId != null ? String(data.categoryId) : "",
            lessonTypeId: data.lessonTypeId != null ? String(data.lessonTypeId) : "",
            imageFile: null,
            preview: null,
        });
        setSaveError("");
        setModal(true);
    };

    // revoke preview when modal closes
    useEffect(() => {
        if (!modal && form.preview) {
            try {
                URL.revokeObjectURL(form.preview);
            } catch (e) { }
            setForm((f) => ({ ...f, preview: null, imageFile: null }));
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [modal]);

    const save = async () => {
        if (!form.title.trim() || !form.categoryId || !form.lessonTypeId) return;
        setSaving(true);
        setSaveError("");
        try {
            const api = authApis();
            const data = new FormData();
            data.append("title", form.title.trim());
            data.append("content", form.content || "");
            data.append("categoryId", form.categoryId);
            data.append("lessonTypeId", form.lessonTypeId);
            if (form.imageFile) {
                data.append("image", form.imageFile);
            }

            if (editing?.id) await api.put(endpoints.lessonById(editing.id), data);
            else await api.post(endpoints.lessons, data);

            setModal(false);
            await load();
        } catch (err) {
            console.error(err);
            setSaveError("Lưu thất bại!");
        } finally {
            setSaving(false);
        }
    };

    const remove = async (row) => {
        if (!window.confirm(`Xóa bài học "${row.title}"?`)) return;
        const api = authApis();
        await api.delete(endpoints.lessonById(row.id));
        await load();
    };

    const filtered = list.filter((l) => {
        const key = q.trim().toLowerCase();
        if (!key) return true;
        return (
            String(l.id).includes(key) ||
            (l.title ?? "").toLowerCase().includes(key) ||
            (l.category?.name ?? "").toLowerCase().includes(key) ||
            (l.lessonType?.name ?? "").toLowerCase().includes(key)
        );
    });

    return (
        <>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Quản lý bài học</h3>
                <Button variant="success" onClick={openNew} disabled={loading}>
                    Thêm bài học
                </Button>
            </div>
            <Row className="mb-3 g-2">
                <Col md={6}>
                    <Form.Control
                        placeholder="Tìm theo tiêu đề, danh mục, loại bài..."
                        value={q}
                        onChange={(e) => setQ(e.target.value)}
                    />
                </Col>
                <Col md={6} className="text-md-end text-muted small d-flex align-items-center justify-content-md-end">
                    Tổng bài học: {filtered.length}
                </Col>
            </Row>
            {loading ? (
                <Spinner animation="border" />
            ) : (
                <Table responsive striped bordered hover size="sm" className="bg-white shadow-sm">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tiêu đề</th>
                            <th>Danh mục</th>
                            <th>Loại bài</th>
                            <th style={{ width: 220 }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {filtered.map((l) => (
                            <tr key={l.id}>
                                <td>{l.id}</td>
                                <td>{l.title}</td>
                                <td>{l.category?.name ?? `#${l.category?.id ?? "-"}`}</td>
                                <td>{l.lessonType?.name ?? `#${l.lessonType?.id ?? "-"}`}</td>
                                <td>
                                    <Button size="sm" variant="outline-primary" className="me-1" onClick={() => openEdit(l)}>
                                        Sửa
                                    </Button>
                                    <Button
                                        as={Link}
                                        size="sm"
                                        variant="outline-secondary"
                                        className="me-1"
                                        to={`/admin/lessons/${l.id}/sections`}
                                    >
                                        Section
                                    </Button>
                                    <Button size="sm" variant="outline-danger" onClick={() => remove(l)}>
                                        Xóa
                                    </Button>
                                </td>
                            </tr>
                        ))}
                        {filtered.length === 0 && (
                            <tr>
                                <td colSpan={5} className="text-center text-muted">
                                    Không có lesson phù hợp.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            )}

            <Modal show={modal} onHide={() => setModal(false)} size="lg" centered scrollable>
                <Modal.Header closeButton>
                    <Modal.Title>{editing ? "Sửa bài học" : "Thêm bài học"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {saveError && (
                        <div className="text-danger small mb-2">{saveError}</div>
                    )}
                    <Form.Group className="mb-3">
                        <Form.Label>Tiêu đề</Form.Label>
                        <Form.Control value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Ảnh (chọn từ máy)</Form.Label>
                        <Form.Control
                            type="file"
                            accept="image/*"
                            onChange={handleFileChange}
                        />
                        {(form.preview || form.imageUrl) && (
                            <div className="mt-2">
                                <img src={form.preview || form.imageUrl} alt="preview" style={{ maxWidth: 220, maxHeight: 160 }} />
                            </div>
                        )}
                    </Form.Group>
                    <Row className="g-2 mb-3">
                        <Col md={6}>
                            <Form.Label>Danh mục</Form.Label>
                            <Form.Select
                                value={form.categoryId}
                                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                            >
                                <option value="">-- Chọn danh mục --</option>
                                {categories.map((c) => (
                                    <option key={c.id} value={String(c.id)}>
                                        {c.name}
                                    </option>
                                ))}
                            </Form.Select>
                        </Col>
                        <Col md={6}>
                            <Form.Label>Loại bài học</Form.Label>
                            <Form.Select
                                value={form.lessonTypeId}
                                onChange={(e) => setForm({ ...form, lessonTypeId: e.target.value })}
                            >
                                <option value="">-- Chọn loại bài học --</option>
                                {lessonTypes.map((t) => (
                                    <option key={t.id} value={String(t.id)}>
                                        {t.name} {t.skill ? `(${t.skill})` : ""}
                                    </option>
                                ))}
                            </Form.Select>
                        </Col>
                    </Row>
                    <Form.Group>
                        <Form.Label>Nội dung (HTML hoặc URL listening)</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={8}
                            value={form.content}
                            onChange={(e) => setForm({ ...form, content: e.target.value })}
                        />
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setModal(false)}>
                        Hủy
                    </Button>
                    <Button variant="success" onClick={save} disabled={saving}>
                        {saving ? "Đang lưu..." : "Lưu"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default AdminLessons;
