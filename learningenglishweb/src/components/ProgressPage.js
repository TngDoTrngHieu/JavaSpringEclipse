import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Form, Modal, Row, Spinner } from "react-bootstrap";
import { Navigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "./configs";

const SKILL_COLORS = {
  READING:   { bg: "#e8f4fd", color: "#1565c0", bar: "#1976d2" },
  LISTENING: { bg: "#f3e5f5", color: "#6a1b9a", bar: "#8e24aa" },
  WRITING:   { bg: "#e8f5e9", color: "#1b5e20", bar: "#2e7d32" },
  SPEAKING:  { bg: "#fff3e0", color: "#bf360c", bar: "#e64a19" },
};

const SKILL_LABELS = {
  READING: "Reading", LISTENING: "Listening",
  WRITING: "Writing", SPEAKING: "Speaking",
};

const formatDate = (dt) => {
  if (!dt) return "";
  return new Date(dt).toLocaleDateString("vi-VN", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
};

const formatDuration = (secs) => {
  if (!secs) return "0 phút";
  const m = Math.floor(secs / 60);
  const s = secs % 60;
  return m > 0 ? `${m} phút ${s > 0 ? s + "s" : ""}` : `${s}s`;
};

const daysLeft = (createdAt, durationDays) => {
  if (!createdAt || !durationDays) return 0;
  const end = new Date(createdAt);
  end.setDate(end.getDate() + durationDays);
  const diff = Math.ceil((end - new Date()) / (1000 * 60 * 60 * 24));
  return Math.max(0, diff);
};

export default function ProgressPage() {
  const token = cookie.load("token");
  const [planData, setPlanData] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ durationDays: 30, goalScore: 7.0 });
  const [creating, setCreating] = useState(false);

  const load = async () => {
    try {
      setLoading(true);
      setErr("");
      const [planRes, histRes] = await Promise.all([
        authApis().get(endpoints.studyPlanMy),
        authApis().get(endpoints.practiceHistory),
      ]);
      setPlanData(planRes.data);
      setHistory(Array.isArray(histRes.data) ? histRes.data.slice(0, 10) : []);
    } catch {
      setErr("Không thể tải dữ liệu tiến độ.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { if (token) load(); }, [token]);

  const createPlan = async () => {
    try {
      setCreating(true);
      await authApis().post(endpoints.studyPlanCreate, form);
      setShowModal(false);
      load();
    } catch {
      setErr("Không thể tạo kế hoạch.");
    } finally {
      setCreating(false);
    }
  };

  if (!token) return <Navigate to="/login" replace />;

  const hasPlan = planData?.hasPlan;
  const plan = planData?.plan;
  const progress = planData?.progress || [];

  const skillMap = {};
  for (const p of progress) {
    skillMap[p.skill] = parseFloat(p.score);
  }

  return (
    <Container className="py-4" style={{ maxWidth: 900 }}>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1 fw-bold">Tiến độ học tập</h4>
          <p className="text-muted mb-0 small">Theo dõi điểm số và lịch sử luyện tập của bạn.</p>
        </div>
        <Button variant="success" onClick={() => setShowModal(true)}>
          {hasPlan ? "Tạo kế hoạch mới" : "+ Tạo kế hoạch"}
        </Button>
      </div>

      {err && <Alert variant="danger">{err}</Alert>}
      {loading ? (
        <div className="text-center py-5"><Spinner animation="border" variant="success" /></div>
      ) : (
        <>
          {/* Study Plan */}
          {hasPlan ? (
            <Card className="border-0 shadow-sm mb-4">
              <Card.Body>
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <div className="fw-semibold">Kế hoạch hiện tại</div>
                  <Badge bg="success">Đang thực hiện</Badge>
                </div>
                <Row className="g-3">
                  <Col xs={4} className="text-center">
                    <div className="text-muted small mb-1">Mục tiêu Band</div>
                    <div className="fs-3 fw-bold text-success">{parseFloat(plan.goalScore).toFixed(1)}</div>
                  </Col>
                  <Col xs={4} className="text-center">
                    <div className="text-muted small mb-1">Thời gian</div>
                    <div className="fs-3 fw-bold">{plan.durationDays} ngày</div>
                  </Col>
                  <Col xs={4} className="text-center">
                    <div className="text-muted small mb-1">Còn lại</div>
                    <div className={`fs-3 fw-bold ${daysLeft(plan.createdAt, plan.durationDays) <= 7 ? "text-danger" : "text-primary"}`}>
                      {daysLeft(plan.createdAt, plan.durationDays)} ngày
                    </div>
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          ) : (
            <Alert variant="light" className="mb-4">
              Bạn chưa có kế hoạch học tập. Tạo mới để theo dõi tiến độ!
            </Alert>
          )}

          <div className="fw-semibold mb-3">Điểm theo kỹ năng</div>
          <Row className="g-3 mb-4">
            {["READING", "LISTENING", "WRITING", "SPEAKING"].map((skill) => {
              const score = skillMap[skill] ?? null;
              const c = SKILL_COLORS[skill];
              const pct = score ? (score / 9) * 100 : 0;
              return (
                <Col xs={6} md={3} key={skill}>
                  <Card className="border-0 h-100" style={{ background: c.bg }}>
                    <Card.Body className="py-3 px-3">
                      <div className="small fw-semibold mb-2" style={{ color: c.color }}>
                        {SKILL_LABELS[skill]}
                      </div>
                      <div className="fs-2 fw-bold mb-2" style={{ color: c.color }}>
                        {score !== null ? score.toFixed(1) : "—"}
                      </div>
                      <div style={{ height: 6, background: "#fff", borderRadius: 3, overflow: "hidden" }}>
                        <div style={{
                          width: `${pct}%`, height: "100%",
                          background: c.bar, borderRadius: 3,
                          transition: "width 0.6s ease"
                        }} />
                      </div>
                      {hasPlan && score !== null && (
                        <div className="small mt-2" style={{ color: c.color }}>
                          {score >= parseFloat(plan.goalScore)
                            ? "✓ Đạt mục tiêu"
                            : `Cần +${(parseFloat(plan.goalScore) - score).toFixed(1)}`}
                        </div>
                      )}
                      {score === null && (
                        <div className="small mt-2 text-muted">Chưa có dữ liệu</div>
                      )}
                    </Card.Body>
                  </Card>
                </Col>
              );
            })}
          </Row>

          {/* Practice History */}
          <div className="fw-semibold mb-3">Lịch sử luyện tập gần đây</div>
          {history.length === 0 ? (
            <Alert variant="light">Chưa có lịch sử luyện tập.</Alert>
          ) : (
            <Card className="border-0 shadow-sm">
              <Card.Body className="p-0">
                {history.map((h, i) => {
                  const skill = h.lesson?.lessonType?.skill || "READING";
                  const c = SKILL_COLORS[skill] || SKILL_COLORS.READING;
                  return (
                    <div key={h.id}
                      className="d-flex align-items-center px-4 py-3"
                      style={{ borderBottom: i < history.length - 1 ? "1px solid #f0f0f0" : "none" }}
                    >
                      <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0 me-3"
                        style={{ width: 40, height: 40, background: c.bg, color: c.color, fontSize: 13, fontWeight: 600 }}>
                        {(SKILL_LABELS[skill] || "?").slice(0, 2).toUpperCase()}
                      </div>
                      <div className="flex-grow-1">
                        <div className="fw-semibold small">{h.lesson?.title || "Bài học"}</div>
                        <div className="text-muted" style={{ fontSize: 12 }}>
                          {formatDate(h.createdAt)} · {formatDuration(h.durationSeconds)}
                        </div>
                      </div>
                      <Badge style={{ background: c.bar, fontSize: 13 }}>
                        {parseFloat(h.score).toFixed(1)}
                      </Badge>
                    </div>
                  );
                })}
              </Card.Body>
            </Card>
          )}
        </>
      )}

      {/* Modal tạo plan */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Tạo kế hoạch học tập</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Mục tiêu Band score</Form.Label>
              <Form.Control
                type="number" min="1" max="9" step="0.5"
                value={form.goalScore}
                onChange={(e) => setForm({ ...form, goalScore: parseFloat(e.target.value) })}
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>Thời gian (ngày)</Form.Label>
              <Form.Select
                value={form.durationDays}
                onChange={(e) => setForm({ ...form, durationDays: parseInt(e.target.value) })}
              >
                {[15, 30, 60, 90].map((d) => (
                  <option key={d} value={d}>{d} ngày</option>
                ))}
              </Form.Select>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setShowModal(false)}>Hủy</Button>
          <Button variant="success" onClick={createPlan} disabled={creating}>
            {creating ? <Spinner size="sm" className="me-2" /> : null}
            Tạo kế hoạch
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}