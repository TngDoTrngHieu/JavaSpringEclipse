import { useEffect, useMemo, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs.js";
import MySpinner from "./layout/MySpiner";

const SKILL_OPTIONS = [
    { value: "", label: "Tất cả kỹ năng" },
    { value: "READING", label: "Reading" },
    { value: "LISTENING", label: "Listening" },
    { value: "WRITING", label: "Writing" },
    { value: "SPEAKING", label: "Speaking" },
];

const Dashboard = () => {
    const token = cookie.load("token");
    const location = useLocation();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");
    const [lessons, setLessons] = useState([]);
    const [categories, setCategories] = useState([]);
    const [skill, setSkill] = useState("");
    const [categoryId, setCategoryId] = useState("");

    useEffect(() => {
        const q = new URLSearchParams(location.search);
        setSkill(q.get("skill") ?? "");
        setCategoryId(q.get("categoryId") ?? "");
    }, [location.search]);

    useEffect(() => {
        if (!token) return;
        const api = authApis();
        const params = {};
        if (skill) params.skill = skill;
        if (categoryId) params.categoryId = categoryId;

        (async () => {
            setLoading(true);
            setErr("");
            try {
                const [lRes, cRes] = await Promise.all([
                    api.get(endpoints.lessons, { params }),
                    api.get(endpoints.categories),
                ]);
                setLessons(Array.isArray(lRes.data) ? lRes.data : []);
                setCategories(Array.isArray(cRes.data) ? cRes.data : []);
            } catch (e) {
                const d = e?.response?.data;
                setErr(typeof d === "string" ? d : (d?.error ?? d?.message ?? "Không tải được dashboard."));
                setLessons([]);
            } finally {
                setLoading(false);
            }
        })();
    }, [token, skill, categoryId]);

    const totalBySkill = useMemo(() => {
        const counters = { READING: 0, LISTENING: 0, WRITING: 0, SPEAKING: 0 };
        for (const l of lessons) {
            const s = l?.lessonType?.skill;
            if (s && counters[s] != null) counters[s] += 1;
        }
        return counters;
    }, [lessons]);

    if (!token) return <Navigate to="/login?next=%2Fdashboard" replace />;

    const applyFilters = (nextSkill, nextCategory) => {
        const q = new URLSearchParams();
        if (nextSkill) q.set("skill", nextSkill);
        if (nextCategory) q.set("categoryId", nextCategory);
        navigate(`/dashboard${q.toString() ? `?${q.toString()}` : ""}`);
    };



    return (
        <Container className="py-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h3 className="mb-1">Dashboard học tập</h3>
                    <p className="text-muted mb-0">Tất cả bài học được lọc theo kỹ năng và danh mục.</p>
                </div>
                <Button variant="outline-secondary" onClick={() => applyFilters("", "")}>
                    Xóa lọc
                </Button>
            </div>

            <Row className="g-3 mb-3">
                <Col md={6}>
                    <Form.Label>Kỹ năng</Form.Label>
                    <Form.Select value={skill} onChange={(e) => applyFilters(e.target.value, categoryId)}>
                        {SKILL_OPTIONS.map((s) => (
                            <option key={s.value || "all"} value={s.value}>
                                {s.label}
                            </option>
                        ))}
                    </Form.Select>
                </Col>
                <Col md={6}>
                    <Form.Label>Danh mục</Form.Label>
                    <Form.Select value={categoryId} onChange={(e) => applyFilters(skill, e.target.value)}>
                        <option value="">Tất cả danh mục</option>
                        {categories.map((c) => (
                            <option key={c.id} value={String(c.id)}>
                                {c.name}
                            </option>
                        ))}
                    </Form.Select>
                </Col>
            </Row>

            <div className="mb-3">
                {Object.entries(totalBySkill).map(([k, v]) => (
                    <Badge key={k} bg="secondary" className="me-1">
                        {k}: {v}
                    </Badge>
                ))}
            </div>

            {err && <Alert variant="danger">{err}</Alert>}
            {loading ? (
                <MySpinner />
            ) : lessons.length === 0 ? (
                <Alert variant="light">Không có bài học phù hợp bộ lọc hiện tại.</Alert>
            ) : (
                <Row className="g-3">
                    {lessons.map((lesson) => {


                        return (
                            <Col md={6} lg={4}>
                                <Card
                                    className="h-100 shadow-sm border-0"

                                    onClick={() => {
                                        const skillType = lesson?.lessonType?.skill;
                                        const typeName = (lesson?.lessonType?.name || "").toLowerCase();

                                        if (skillType === "WRITING" || typeName.includes("writing")) {
                                            navigate(`/writing/${lesson.id}`);
                                        } else if (skillType === "SPEAKING" || typeName.includes("speaking")) {
                                            navigate(`/speaking/${lesson.id}`);
                                        } else if (skillType === "LISTENING" || typeName.includes("listening")) {
                                            navigate(`/listening/${lesson.id}`);
                                        }
                                        else if (skillType === "READING" || typeName.includes("reading")) {
                                            navigate(`/reading/${lesson.id}`);
                                        }
                                    }}
                                >
                                    {lesson.imageUrl ? (
                                        <Card.Img
                                            variant="top"
                                            src={lesson.imageUrl}
                                            alt=""
                                            style={{ maxHeight: 160, objectFit: "cover" }}
                                        />
                                    ) : null}
                                    <Card.Body>
                                        <Card.Title className="fs-6">{lesson.title}</Card.Title>
                                        <div className="small text-muted">
                                            {lesson.category?.name ?? "Không có danh mục"}
                                            {" · "}
                                            {lesson.lessonType?.name ?? "Không có loại bài"}
                                        </div>

                                    </Card.Body>
                                </Card>
                            </Col>
                        );
                    })}
                </Row>
            )}
        </Container>
    );
};

export default Dashboard;
