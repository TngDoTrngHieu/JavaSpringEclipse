import { useEffect, useState } from "react";
import { Button, Form, Modal, Spinner, Table } from "react-bootstrap";
import { authApis, endpoints } from "../configs.js";
import "./admin.css";

const AdminCategories = () => {
    const [list, setList] = useState([]);
    const [types, setTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modal, setModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [form, setForm] = useState({ name: "", categoryTypeId: "" });
    const [saving, setSaving] = useState(false);
    const [newTypeName, setNewTypeName] = useState("");
    const [creatingType, setCreatingType] = useState(false);

    useEffect(() => {
        const api = authApis();
        (async () => {
            setLoading(true);
            const [c, t] = await Promise.all([
                api.get(endpoints.categories),
                api.get(endpoints.categoryTypes),
            ]);
            setList(Array.isArray(c.data) ? c.data : []);
            setTypes(Array.isArray(t.data) ? t.data : []);
            setLoading(false);
        })();
    }, []);

    const openNew = () => {
        setEditing(null);
        setForm({ name: "", categoryTypeId: types[0]?.id ? String(types[0].id) : "" });
        setModal(true);
    };

    const openEdit = (row) => {
        setEditing(row);
        setForm({
            name: row.name ?? "",
            categoryTypeId: row.categoryType?.id != null ? String(row.categoryType.id) : "",
        });
        setModal(true);
    };

    const save = async () => {
        if (!form.name.trim() || !form.categoryTypeId) return;
        setSaving(true);
        const api = authApis();
        const body = {
            name: form.name.trim(),
            categoryType: { id: Number(form.categoryTypeId) },
        };
        if (editing) {
            await api.put(endpoints.categoryById(editing.id), body);
        } else {
            await api.post(endpoints.categories, body);
        }
        setModal(false);
        const [c, t] = await Promise.all([
            api.get(endpoints.categories),
            api.get(endpoints.categoryTypes),
        ]);
        setList(Array.isArray(c.data) ? c.data : []);
        setTypes(Array.isArray(t.data) ? t.data : []);
        setSaving(false);
    };

    const remove = async (row) => {
        if (!window.confirm(`Xóa danh mục "${row.name}"?`)) return;
        const api = authApis();
        await api.delete(endpoints.categoryById(row.id));
        const c = await api.get(endpoints.categories);
        setList(Array.isArray(c.data) ? c.data : []);
    };

    const createType = async () => {
        if (!newTypeName.trim()) return;
        setCreatingType(true);
        const api = authApis();
        await api.post(endpoints.categoryTypes, { name: newTypeName.trim() });
        setNewTypeName("");
        const t = await api.get(endpoints.categoryTypes);
        setTypes(Array.isArray(t.data) ? t.data : []);
        setCreatingType(false);
    };

    return (
        <>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Quản lý danh mục</h3>
                <Button variant="success" onClick={openNew} disabled={loading}>
                    Thêm danh mục
                </Button>
            </div>
            {types.length === 0 && !loading && (
                <div className="text-muted small mb-2">Chưa có loại danh mục — tạo nhanh bên dưới.</div>
            )}
            <div className="bg-white border rounded p-3 mb-3">
                <div className="fw-semibold mb-2">Tạo loại danh mục</div>
                <div className="d-flex gap-2">
                    <Form.Control
                        placeholder="Ví dụ: IELTS Reading"
                        value={newTypeName}
                        onChange={(e) => setNewTypeName(e.target.value)}
                    />
                    <Button variant="outline-success" onClick={createType} disabled={creatingType}>
                        {creatingType ? "Đang tạo..." : "Tạo"}
                    </Button>
                </div>
            </div>
            {loading ? (
                <Spinner animation="border" />
            ) : (
                <Table responsive striped bordered hover size="sm" className="bg-white shadow-sm">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên</th>
                            <th>Loại danh mục</th>
                            <th style={{ width: 160 }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {list.map((c) => (
                            <tr key={c.id}>
                                <td>{c.id}</td>
                                <td>{c.name}</td>
                                <td>{c.categoryType?.name ?? "—"}</td>
                                <td>
                                    <Button size="sm" variant="outline-primary" className="me-1" onClick={() => openEdit(c)}>
                                        Sửa
                                    </Button>
                                    <Button size="sm" variant="outline-danger" onClick={() => remove(c)}>
                                        Xóa
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}

            <Modal show={modal} onHide={() => setModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{editing ? "Sửa danh mục" : "Thêm danh mục"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3">
                        <Form.Label>Tên danh mục</Form.Label>
                        <Form.Control value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Loại danh mục</Form.Label>
                        {types.length > 0 ? (
                            <Form.Select
                                value={form.categoryTypeId}
                                onChange={(e) => setForm({ ...form, categoryTypeId: e.target.value })}
                            >
                                {types.map((t) => (
                                    <option key={t.id} value={String(t.id)}>
                                        {t.name}
                                    </option>
                                ))}
                            </Form.Select>
                        ) : (
                            <Form.Control
                                placeholder="Nhập category type id"
                                value={form.categoryTypeId}
                                onChange={(e) => setForm({ ...form, categoryTypeId: e.target.value })}
                            />
                        )}
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setModal(false)}>
                        Hủy
                    </Button>
                    <Button variant="success" onClick={save} disabled={saving}>
                        {saving ? "Đang lưu…" : "Lưu"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default AdminCategories;
