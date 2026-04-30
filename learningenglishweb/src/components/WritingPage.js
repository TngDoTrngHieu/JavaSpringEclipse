import { useEffect, useState, useRef, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Row, Col, Card, Button, Form, Badge, ProgressBar, Modal, Spinner, Alert } from "react-bootstrap";
import { authApis, endpoints } from "./configs.js";

const TASK2_MINUTES = 40;
const TASK1_MINUTES = 20;

export default function WritingPage() {
  const { lessonId } = useParams();
  const navigate = useNavigate();

  const [lesson, setLesson] = useState(null);
  const [essay, setEssay] = useState("");
  const [seconds, setSeconds] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const timerRef = useRef(null);
  const startTimeRef = useRef(Date.now());

  const isTask1 = lesson?.lessonTypeName?.toLowerCase().includes("task 1");
  const totalSeconds = (isTask1 ? TASK1_MINUTES : TASK2_MINUTES) * 60;
  const remaining = totalSeconds - seconds;
  const wordCount = essay.trim() ? essay.trim().split(/\s+/).length : 0;
  const minWords = isTask1 ? 150 : 250;

  useEffect(() => {
    const load = async () => {
      try {
        const res = await authApis().get(endpoints.lessonById(lessonId));
        setLesson(res.data);
      } catch {
        setError("Không thể tải bài học.");
      }
    };
    load();
  }, [lessonId]);

  useEffect(() => {
    timerRef.current = setInterval(() => {
      setSeconds((s) => s + 1);
    }, 1000);
    return () => clearInterval(timerRef.current);
  }, []);

  const formatTime = (secs) => {
    const m = Math.floor(Math.abs(secs) / 60).toString().padStart(2, "0");
    const s = (Math.abs(secs) % 60).toString().padStart(2, "0");
    return `${secs < 0 ? "-" : ""}${m}:${s}`;
  };

  const submit = useCallback(async () => {
    if (!essay.trim()) { setError("Vui lòng nhập bài viết."); return; }
    try {
      setError("");
      setSubmitting(true);
      clearInterval(timerRef.current);
      const duration = Math.floor((Date.now() - startTimeRef.current) / 1000);
      const res = await authApis().post(endpoints.writingSubmit, {
        content: essay,
        lessonId: Number(lessonId),
        durationSeconds: duration,
      });
      setResult(res.data);
    } catch (e) {
      setError(e?.response?.data?.error || "Lỗi khi nộp bài. Thử lại sau.");
      timerRef.current = setInterval(() => setSeconds((s) => s + 1), 1000);
    } finally {
      setSubmitting(false);
    }
  }, [essay, lessonId]);

  const resetForm = () => {
    setResult(null);
    setEssay("");
    setSeconds(0);
    startTimeRef.current = Date.now();
    timerRef.current = setInterval(() => setSeconds((s) => s + 1), 1000);
  };

  if (!lesson && !error) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2 text-muted">Đang tải bài học...</p>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4 bg-light" style={{ minHeight: "100vh" }}>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <Button variant="outline-secondary" size="sm" onClick={() => navigate(-1)}>
            &larr; Quay lại
          </Button>
          <span className="ms-3 fw-bold fs-5">{lesson?.title}</span>
        </div>
        <div className="d-flex align-items-center gap-3">
          <Badge
            bg={remaining < 0 ? "danger" : remaining < 300 ? "warning" : "primary"}
            className="fs-5"
          >
            {formatTime(remaining)}
          </Badge>
          <Button variant="success" onClick={submit} disabled={submitting || !essay.trim()}>
            {submitting ? <><Spinner size="sm" /> Đang chấm...</> : "Nộp bài"}
          </Button>
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* Main Content */}
      <Row>
        {/* Cột Đề bài */}
        <Col md={5} className="mb-3">
          <Card className="h-100 shadow-sm">
            <Card.Header className="bg-white">
              <Badge bg="info" className="me-2">{isTask1 ? "Task 1" : "Task 2"}</Badge>
              <strong>Đề bài</strong>
            </Card.Header>
            <Card.Body style={{ overflowY: "auto", maxHeight: "70vh" }}>
              {lesson?.imageUrl && (
                <img src={lesson.imageUrl} alt="Prompt visual" className="img-fluid rounded mb-3 border" />
              )}
              {lesson?.content && (
                <p style={{ whiteSpace: "pre-line", fontSize: "16px" }}>{lesson.content}</p>
              )}
              <Alert variant="info" className="mt-3 py-2 text-center">
                Yêu cầu: Viết tối thiểu <strong>{minWords}</strong> từ trong <strong>{isTask1 ? TASK1_MINUTES : TASK2_MINUTES}</strong> phút.
              </Alert>
            </Card.Body>
          </Card>
        </Col>

        {/* Cột Khung viết bài */}
        <Col md={7} className="mb-3">
          <Card className="h-100 shadow-sm">
            <Card.Header className="bg-white d-flex justify-content-between align-items-center">
              <strong>Bài làm của bạn</strong>
              <div className="text-muted small">
                <span className={wordCount >= minWords ? "text-success fw-bold" : ""}>
                  {wordCount} / {minWords} từ
                </span>
              </div>
            </Card.Header>
            <Card.Body className="p-0">
              <Form.Control
                as="textarea"
                className="border-0 p-3"
                style={{ height: "70vh", resize: "none", boxShadow: "none" }}
                value={essay}
                onChange={(e) => setEssay(e.target.value)}
                placeholder="Bắt đầu viết bài của bạn tại đây..."
                disabled={submitting}
              />
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Popup Kết quả (Modal) */}
      <Modal show={result !== null} onHide={() => setResult(null)} backdrop="static" size="lg" centered>
        <Modal.Header>
          <Modal.Title className="text-success fw-bold">Kết quả chấm bài</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="text-center mb-4">
            <h1 className="display-4 text-primary fw-bold">{result?.overallScore ? parseFloat(result.overallScore).toFixed(1) : "N/A"}</h1>
            <span className="text-muted text-uppercase">Overall Band Score</span>
          </div>

          <Row className="mb-4 g-3">
            {[
              { label: "Task Response", val: result?.taskScore },
              { label: "Coherence & Cohesion", val: result?.coherenceScore },
              { label: "Lexical Resource", val: result?.lexicalScore },
              { label: "Grammar", val: result?.grammarScore },
            ].map((s, idx) => (
              <Col sm={6} key={idx}>
                <div className="d-flex justify-content-between small fw-bold mb-1">
                  <span>{s.label}</span>
                  <span>{s.val ? parseFloat(s.val).toFixed(1) : "0.0"}</span>
                </div>
                <ProgressBar now={(s.val / 9) * 100} variant="success" style={{ height: "6px" }} />
              </Col>
            ))}
          </Row>

          <h6 className="fw-bold border-bottom pb-2">Nhận xét chi tiết:</h6>
          <p style={{ whiteSpace: "pre-line", fontSize: "15px" }} className="text-dark">
            {result?.feedback}
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => navigate(-1)}>
            Trở về danh sách
          </Button>
          <Button variant="primary" onClick={resetForm}>
            Làm lại bài này
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}