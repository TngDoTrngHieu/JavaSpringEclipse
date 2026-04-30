import { useEffect, useState } from "react";
import { Button, Form, Modal, Spinner, Table, InputGroup } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import { authApis, endpoints } from "../configs.js";
import "./admin.css";

const CONTENT_TYPES = ["READING_PASSAGE", "LISTENING_AUDIO"];

const AdminLessonSections = () => {
    const { lessonId } = useParams();
    const id = Number(lessonId);
    const [lesson, setLesson] = useState(null);
    const [sections, setSections] = useState([]);
    const [types, setTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modal, setModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [form, setForm] = useState({
        position: 1,
        sectionTypeId: "",
        content: "",
        audioUrl: "",
        question: "",
        options: ["", ""],
        correctAnswer: "",
    });
    const [saving, setSaving] = useState(false);
    const [audioUploading, setAudioUploading] = useState(false);
    const [viewRow, setViewRow] = useState(null);

    const selectedType = types.find((t) => String(t.id) === String(form.sectionTypeId));

    const parseJsonText = (raw) => {
        if (!raw) return "";
        try {
            const n = JSON.parse(raw);
            if (typeof n === "string") return n;
            if (n && typeof n === "object" && n.text != null) return String(n.text);
            if (n && typeof n === "object" && (n.audioUrl != null || n.audio_url != null)) {
                return String(n.audioUrl || n.audio_url || "");
            }
            return raw;
        } catch {
            return raw;
        }
    };

    const parseListeningContent = (raw) => {
        if (!raw) return { audioUrl: "" };
        try {
            const n = JSON.parse(raw);
            if (n && typeof n === "object") {
                return {
                    audioUrl: String(n.audioUrl || n.audio_url || ""),
                };
            }
        } catch {
            // fallback below
        }
        return { audioUrl: String(raw) };
    };

    const parseCorrectAnswer = (raw) => {
        if (!raw) return "";
        try {
            const n = JSON.parse(raw);
            if (typeof n === "string") return n;
            if (n && typeof n === "object" && n.value != null) return String(n.value);
            return raw;
        } catch {
            return raw;
        }
    };

    const formatOptionsPreview = (raw) => {
        if (!raw) return "—";
        try {
            const o = JSON.parse(raw);
            if (Array.isArray(o)) return o.filter(Boolean).join(" | ");
            if (o && typeof o === "object") {
                return Object.keys(o)
                    .sort()
                    .map((k) => `${k}: ${o[k]}`)
                    .join(" | ");
            }
            return String(raw);
        } catch {
            return String(raw).slice(0, 200);
        }
    };

    const sectionTypeName = (s) => s?.sectionType?.name || "UNKNOWN";

    const previewCell = (s) => {
        const name = sectionTypeName(s);
        if (name === "LISTENING_AUDIO") {
            const listening = parseListeningContent(s.content);
            return listening.audioUrl || "Audio section";
        }
        const text = CONTENT_TYPES.includes(name) ? parseJsonText(s.content) : parseJsonText(s.question);
        const t = text || "";
        return t.length > 80 ? `${t.slice(0, 80)}…` : t;
    };

    const loadData = async () => {
        setLoading(true);
        const api = authApis();
        const [les, sec, st] = await Promise.all([
            api.get(endpoints.lessonById(id)),
            api.get(endpoints.sectionsByLesson(id)),
            api.get(endpoints.sectionTypes),
        ]);
        setLesson(les.data);
        setSections(Array.isArray(sec.data) ? sec.data : []);
        setTypes(Array.isArray(st.data) ? st.data : []);
        setLoading(false);
    };

    useEffect(() => {
        if (id) loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);

    const openNew = () => {
        const maxPos = sections.reduce((m, s) => Math.max(m, s.position || 0), 0);
        setEditing(null);
        setForm({
            position: maxPos + 1,
            sectionTypeId: types[0]?.id ? String(types[0].id) : "",
            content: "",
            audioUrl: "",
            question: "",
            options: ["", ""],
            correctAnswer: "",
        });
        setModal(true);
    };

    const openEdit = (row) => {
        let parsedOptions = ["", ""];
        try {
            const parsed = JSON.parse(row.options || "[]");
            parsedOptions = Array.isArray(parsed) ? parsed : Object.values(parsed);
        } catch {
            parsedOptions = ["", ""];
        }

        setEditing(row);
        const listening = parseListeningContent(row.content);
        setForm({
            position: row.position ?? 1,
            sectionTypeId: row.sectionType?.id ? String(row.sectionType.id) : "",
            content: parseJsonText(row.content),
            audioUrl: listening.audioUrl,
            question: parseJsonText(row.question),
            options: parsedOptions.length > 0 ? parsedOptions : ["", ""],
            correctAnswer: parseCorrectAnswer(row.correctAnswer),
        });
        setModal(true);
    };

    const handleTypeChange = (typeId) => {
        setForm({
            ...form,
            sectionTypeId: typeId,
            content: "",
            audioUrl: "",
            question: "",
            options: ["", ""],
            correctAnswer: "",
        });
    };

    const updateOption = (idx, val) => {
        const newOpts = [...form.options];
        newOpts[idx] = val;
        setForm({ ...form, options: newOpts });
    };

    const addOption = () => setForm({ ...form, options: [...form.options, ""] });

    const removeOption = (idx) => {
        const newOpts = form.options.filter((_, i) => i !== idx);
        setForm({ ...form, options: newOpts });
    };

    const save = async () => {
        setSaving(true);
        const api = authApis();
        let contentValue = form.content || null;
        if (selectedType?.name === "LISTENING_AUDIO") {
            contentValue = JSON.stringify({
                audioUrl: form.audioUrl || "",
            });
        }
        const body = {
            position: Number(form.position),
            content: contentValue,
            question: form.question || null,
            options:
                selectedType?.name === "MULTIPLE_CHOICE"
                    ? JSON.stringify(form.options.filter((o) => o.trim() !== ""))
                    : null,
            correctAnswer: form.correctAnswer || null,
            lesson: { id },
            sectionType: { id: Number(form.sectionTypeId) },
        };

        if (editing?.id) {
            await api.put(endpoints.sectionById(editing.id), body);
        } else {
            await api.post(endpoints.sections, body);
        }
        setModal(false);
        loadData();
        setSaving(false);
    };

    const uploadListeningAudio = async (file) => {
        if (!file) return;
        setAudioUploading(true);
        try {
            const fd = new FormData();
            fd.append("audio", file);
            const res = await authApis().post(endpoints.sectionUploadAudio, fd);
            setForm((prev) => ({ ...prev, audioUrl: res.data?.audioUrl || "" }));
        } finally {
            setAudioUploading(false);
        }
    };

    return (
        <>
            <div className="mb-3">
                <Link to="/admin/lessons">← Danh sách bài học</Link>
            </div>
            <h3>Section của bài học</h3>
            {lesson && (
                <p className="text-muted">
                    #{lesson.id} — {lesson.title}
                </p>
            )}

            <div className="d-flex justify-content-end mb-2">
                <Button variant="success" onClick={openNew} disabled={loading || types.length === 0}>
                    Thêm section
                </Button>
            </div>

            {loading ? (
                <Spinner animation="border" />
            ) : (
                <Table responsive striped bordered hover size="sm" className="bg-white shadow-sm">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Vị trí</th>
                            <th>Loại section</th>
                            <th>Nội dung / Câu hỏi</th>
                            <th style={{ width: 200 }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {sections.map((s) => (
                            <tr key={s.id}>
                                <td>{s.id}</td>
                                <td>{s.position}</td>
                                <td>
                                    <span className="fw-semibold">{sectionTypeName(s)}</span>
                                </td>
                                <td className="small text-truncate" style={{ maxWidth: 300 }}>
                                    {previewCell(s)}
                                </td>
                                <td>
                                    <Button size="sm" variant="outline-info" className="me-1" onClick={() => setViewRow(s)}>
                                        Xem
                                    </Button>
                                    <Button size="sm" variant="outline-primary" className="me-1" onClick={() => openEdit(s)}>
                                        Sửa
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="outline-danger"
                                        onClick={() => {
                                            authApis().delete(endpoints.sectionById(s.id)).then(loadData);
                                        }}
                                    >
                                        Xóa
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}

            <Modal show={modal} onHide={() => setModal(false)} size="lg" centered scrollable>
                <Modal.Header closeButton>
                    <Modal.Title>{editing ? "Sửa section" : "Thêm section"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3">
                        <Form.Label>Vị trí & Loại Section</Form.Label>
                        <InputGroup>
                            <Form.Control
                                type="number"
                                value={form.position}
                                onChange={(e) => setForm({ ...form, position: e.target.value })}
                            />
                            <Form.Select value={form.sectionTypeId} onChange={(e) => handleTypeChange(e.target.value)}>
                                <option value="">— Chọn loại section —</option>
                                {types.map((t) => (
                                    <option key={t.id} value={String(t.id)}>
                                        {t.name}
                                        {t.saveType != null ? ` (${t.saveType})` : ""}
                                    </option>
                                ))}
                            </Form.Select>
                        </InputGroup>
                    </Form.Group>

                    {selectedType?.name === "READING_PASSAGE" && (
                        <Form.Group className="mb-3">
                            <Form.Label>Nội dung văn bản</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                value={form.content}
                                onChange={(e) => setForm({ ...form, content: e.target.value })}
                            />
                        </Form.Group>
                    )}

                    {selectedType?.name === "LISTENING_AUDIO" && (
                        <>
                            <Form.Group className="mb-3">
                                <Form.Label>Upload audio</Form.Label>
                                <Form.Control
                                    type="file"
                                    accept="audio/*"
                                    disabled={audioUploading}
                                    onChange={(e) => uploadListeningAudio(e.target.files?.[0])}
                                />
                                <Form.Text className="text-muted">
                                    Chọn file audio để upload, hệ thống sẽ tự điền URL.
                                </Form.Text>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Audio URL</Form.Label>
                                <Form.Control
                                    value={form.audioUrl}
                                    onChange={(e) => setForm({ ...form, audioUrl: e.target.value })}
                                    placeholder="https://...mp3"
                                />
                            </Form.Group>
                            {form.audioUrl && (
                                <audio controls className="w-100" src={form.audioUrl} />
                            )}
                        </>
                    )}

                    {["MULTIPLE_CHOICE", "WRITING_TASK", "SPEAKING_TASK"].includes(selectedType?.name) && (
                        <Form.Group className="mb-3">
                            <Form.Label>Câu hỏi (Question)</Form.Label>
                            <Form.Control
                                value={form.question}
                                onChange={(e) => setForm({ ...form, question: e.target.value })}
                            />
                        </Form.Group>
                    )}

                    {selectedType?.name === "MULTIPLE_CHOICE" && (
                        <div className="border p-3 rounded bg-light">
                            <Form.Label className="fw-bold">Danh sách lựa chọn</Form.Label>
                            {form.options.map((opt, idx) => (
                                <InputGroup className="mb-2" key={idx}>
                                    <InputGroup.Text>{String.fromCharCode(65 + idx)}</InputGroup.Text>
                                    <Form.Control
                                        value={opt}
                                        onChange={(e) => updateOption(idx, e.target.value)}
                                        placeholder={`Nhập lựa chọn ${idx + 1}`}
                                    />
                                    <Button
                                        variant="outline-danger"
                                        onClick={() => removeOption(idx)}
                                        disabled={form.options.length <= 2}
                                    >
                                        ✕
                                    </Button>
                                </InputGroup>
                            ))}
                            <Button variant="outline-secondary" size="sm" onClick={addOption}>
                                + Thêm lựa chọn
                            </Button>

                            <Form.Group className="mt-3">
                                <Form.Label className="text-success fw-bold">Đáp án đúng</Form.Label>
                                <Form.Select
                                    value={form.correctAnswer}
                                    onChange={(e) => setForm({ ...form, correctAnswer: e.target.value })}
                                >
                                    <option value="">-- Chọn đáp án đúng --</option>
                                    {form.options
                                        .filter((o) => o.trim() !== "")
                                        .map((opt, i) => (
                                            <option key={i} value={opt}>
                                                {opt}
                                            </option>
                                        ))}
                                </Form.Select>
                            </Form.Group>
                        </div>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setModal(false)}>
                        Hủy
                    </Button>
                    <Button variant="success" onClick={save} disabled={saving}>
                        {saving ? "Đang lưu..." : "Lưu dữ liệu"}
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={!!viewRow} onHide={() => setViewRow(null)} size="lg" centered scrollable>
                <Modal.Header closeButton>
                    <Modal.Title>
                        Section #{viewRow?.id} — {viewRow && sectionTypeName(viewRow)}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {viewRow && (
                        <>
                            <p className="text-muted small mb-3">
                                Vị trí: <strong>{viewRow.position}</strong>
                                {viewRow.sectionType?.id != null && (
                                    <> · Loại ID: <strong>{viewRow.sectionType.id}</strong></>
                                )}
                            </p>
                            <Form.Group className="mb-2">
                                <Form.Label className="fw-semibold">Nội dung (hiển thị)</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={4}
                                    readOnly
                                    value={parseJsonText(viewRow.content)}
                                    className="small"
                                />
                            </Form.Group>
                            <Form.Group className="mb-2">
                                <Form.Label className="fw-semibold">Câu hỏi (hiển thị)</Form.Label>
                                <Form.Control readOnly value={parseJsonText(viewRow.question)} />
                            </Form.Group>
                            <Form.Group className="mb-2">
                                <Form.Label className="fw-semibold">Lựa chọn (tóm tắt)</Form.Label>
                                <Form.Control readOnly value={formatOptionsPreview(viewRow.options)} />
                            </Form.Group>
                            <Form.Group className="mb-0">
                                <Form.Label className="fw-semibold">Đáp án đúng (hiển thị)</Form.Label>
                                <Form.Control readOnly value={parseCorrectAnswer(viewRow.correctAnswer)} />
                            </Form.Group>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setViewRow(null)}>
                        Đóng
                    </Button>
                    {viewRow && (
                        <Button
                            variant="primary"
                            onClick={() => {
                                openEdit(viewRow);
                                setViewRow(null);
                            }}
                        >
                            Sửa section này
                        </Button>
                    )}
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default AdminLessonSections;
