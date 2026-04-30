import { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Alert, Badge, Button, Card, Col, Container, Row, Spinner } from "react-bootstrap";
import { authApis, endpoints } from "./configs";

const SpeakingPage = () => {
    const { lessonId } = useParams();
    const navigate = useNavigate();

    const [lesson, setLesson] = useState(null);
    const [sections, setSections] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    // Recording
    const [recording, setRecording] = useState(false);
    const [audioBlob, setAudioBlob] = useState(null);
    const [audioUrl, setAudioUrl] = useState("");
    const [transcript, setTranscript] = useState("");
    const [duration, setDuration] = useState(0);

    // Submit
    const [submitting, setSubmitting] = useState(false);
    const [result, setResult] = useState(null);

    const mediaRecorderRef = useRef(null);
    const chunksRef = useRef([]);
    const recognitionRef = useRef(null);
    const timerRef = useRef(null);
    const startTimeRef = useRef(null);

    useEffect(() => {
        const load = async () => {
            try {
                const [lessonRes, sectionRes] = await Promise.all([
                    authApis().get(endpoints.lessonById(lessonId)),
                    authApis().get(endpoints.sectionsByLesson(lessonId)),
                ]);
                setLesson(lessonRes.data);
                setSections(Array.isArray(sectionRes.data) ? sectionRes.data : []);
            } catch {
                setErr("Không thể tải bài học.");
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [lessonId]);

    const startRecording = async () => {
        try {
            setErr("");
            setTranscript("");
            setAudioBlob(null);
            setAudioUrl("");
            chunksRef.current = [];

            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const mediaRecorder = new MediaRecorder(stream);
            mediaRecorderRef.current = mediaRecorder;

            mediaRecorder.ondataavailable = (e) => {
                if (e.data.size > 0) chunksRef.current.push(e.data);
            };

            mediaRecorder.onstop = () => {
                const blob = new Blob(chunksRef.current, { type: "audio/webm" });
                setAudioBlob(blob);
                setAudioUrl(URL.createObjectURL(blob));
                stream.getTracks().forEach((t) => t.stop());
            };

            mediaRecorder.start();
            setRecording(true);
            startTimeRef.current = Date.now();

            // Đếm giây
            timerRef.current = setInterval(() => {
                setDuration(Math.floor((Date.now() - startTimeRef.current) / 1000));
            }, 1000);

            // Web Speech API
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            if (SpeechRecognition) {
                const recognition = new SpeechRecognition();
                recognition.lang = "en-US";
                recognition.continuous = true;
                recognition.interimResults = false;
                recognition.onresult = (e) => {
                    const text = Array.from(e.results)
                        .map((r) => r[0].transcript)
                        .join(" ");
                    setTranscript(text);
                };
                recognition.start();
                recognitionRef.current = recognition;
            }
        } catch {
            setErr("Không thể truy cập microphone.");
        }
    };

    const stopRecording = () => {
        if (mediaRecorderRef.current) mediaRecorderRef.current.stop();
        if (recognitionRef.current) recognitionRef.current.stop();
        clearInterval(timerRef.current);
        setRecording(false);
    };

    const submit = async () => {
        if (!transcript.trim()) {
            setErr("Không nhận được giọng nói. Vui lòng thử lại.");
            return;
        }
        try {
            setErr("");
            setSubmitting(true);

            // Upload audio nếu có
            let uploadedUrl = "";
            if (audioBlob) {
                const formData = new FormData();
                formData.append("audio", audioBlob, "recording.webm");
                const uploadRes = await authApis().post(endpoints.speakingUpload, formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
                uploadedUrl = uploadRes.data.audioUrl || "";
            }

            // Submit để chấm
            const res = await authApis().post(endpoints.speakingSubmit, {
                transcript,
                audioUrl: uploadedUrl,
                lessonId: Number(lessonId),
                durationSeconds: duration,
            });
            setResult(res.data);
        } catch (e) {
            setErr(e?.response?.data?.error || "Lỗi khi nộp bài.");
        } finally {
            setSubmitting(false);
        }
    };

    const formatTime = (s) => {
        const m = Math.floor(s / 60).toString().padStart(2, "0");
        const sec = (s % 60).toString().padStart(2, "0");
        return `${m}:${sec}`;
    };

    const questions = sections.filter(
        (s) => s.question && s.question.trim() && s.question !== "null"
    );

    const parseQuestion = (raw) => {
        if (!raw) return null;
        try {
            return JSON.parse(raw);
        } catch {
            return { plain: raw };
        }
    };

    if (loading) return (
        <Container className="py-5 text-center">
            <Spinner animation="border" variant="success" />
        </Container>
    );

    // Kết quả
    if (result) return (
        <Container className="py-4" style={{ maxWidth: 700 }}>
            <div className="d-flex align-items-center gap-3 mb-4">
                <Button variant="outline-secondary" size="sm" onClick={() => navigate(-1)}>← Quay lại</Button>
                <h5 className="mb-0">Kết quả Speaking</h5>
                <Badge bg="success" className="ms-auto fs-6">
                    Band {parseFloat(result.overallScore).toFixed(1)}
                </Badge>
            </div>

            <Row className="g-3 mb-4">
                {[
                    { label: "Pronunciation", val: result.pronunciationScore },
                    { label: "Fluency", val: result.fluencyScore },
                    { label: "Coherence", val: result.coherenceScore },
                    { label: "Lexical", val: result.lexicalScore },
                    { label: "Grammar", val: result.grammarScore },
                ].map((s) => (
                    <Col xs={6} md={4} key={s.label}>
                        <Card className="text-center border-0 bg-light">
                            <Card.Body className="py-3">
                                <div className="small text-muted mb-1">{s.label}</div>
                                <div className="fs-4 fw-semibold">{parseFloat(s.val).toFixed(1)}</div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            {result.transcript && (
                <Card className="border-0 bg-light mb-3">
                    <Card.Body>
                        <div className="small text-muted mb-2 text-uppercase fw-semibold">Transcript</div>
                        <p className="mb-0" style={{ lineHeight: 1.8 }}>{result.transcript}</p>
                    </Card.Body>
                </Card>
            )}

            <Card className="border-0 bg-light mb-4">
                <Card.Body>
                    <div className="small text-muted mb-2 text-uppercase fw-semibold">Nhận xét</div>
                    <p className="mb-0" style={{ lineHeight: 1.8, whiteSpace: "pre-line" }}>{result.feedback}</p>
                </Card.Body>
            </Card>

            <div className="d-flex gap-2">
                <Button variant="dark" onClick={() => navigate(-1)} className="flex-fill">Quay lại danh sách</Button>
                <Button variant="outline-secondary" onClick={() => { setResult(null); setAudioBlob(null); setTranscript(""); setDuration(0); }} className="flex-fill">
                    Làm lại
                </Button>
            </div>
        </Container>
    );

    return (
        <Container className="py-4" style={{ maxWidth: 700 }}>
            {/* Header */}
            <div className="d-flex align-items-center gap-3 mb-4">
                <Button variant="outline-secondary" size="sm" onClick={() => navigate(-1)}>← Quay lại</Button>
                <div>
                    <h5 className="mb-0">{lesson?.title}</h5>
                    <small className="text-muted">IELTS Speaking Practice</small>
                </div>
            </div>

            {err && <Alert variant="danger">{err}</Alert>}

            {/* Câu hỏi */}
            <Card className="border-0 shadow-sm mb-4">
                <Card.Body>
                    <div className="small text-muted text-uppercase fw-semibold mb-3">Câu hỏi</div>
                    {questions.length === 0 ? (
                        <p className="text-muted mb-0">Không có câu hỏi.</p>
                    ) : (
                        <div>
                            {questions.map((s) => {
                                const q = parseQuestion(s.question);
                                if (!q) return null;

                                if (q.plain) {
                                    return (
                                        <ol key={s.id} className="mb-3" style={{ lineHeight: 2 }}>
                                            <li>{q.plain}</li>
                                        </ol>
                                    );
                                }

                                if (q.questions) {
                                    return (
                                        <div key={s.id} className="mb-3">
                                            <div className="fw-semibold mb-1">{q.part}</div>
                                            {q.instruction && <div className="text-muted small mb-2">{q.instruction}</div>}
                                            <ol>
                                                {q.questions.map((question, i) => (
                                                    <li key={i} style={{ lineHeight: 2 }}>{question}</li>
                                                ))}
                                            </ol>
                                        </div>
                                    );
                                }

                                if (q.cueCard) {
                                    return (
                                        <div key={s.id} className="mb-3">
                                            <div className="fw-semibold mb-1">{q.part}</div>
                                            {q.instruction && <div className="text-muted small mb-2">{q.instruction}</div>}
                                            <div className="bg-light rounded p-3">
                                                <div className="fw-semibold mb-2">{q.cueCard.prompt}</div>
                                                <ul className="mb-0">
                                                    {(q.cueCard.points || []).map((p, i) => (
                                                        <li key={i}>{p}</li>
                                                    ))}
                                                </ul>
                                            </div>
                                        </div>
                                    );
                                }

                                return null;
                            })}
                        </div>
                    )}
                </Card.Body>
            </Card>

            {/* Record */}
            <Card className="border-0 shadow-sm mb-4">
                <Card.Body className="text-center py-4">
                    {!recording && !audioBlob && (
                        <>
                            <div className="text-muted mb-3">Nhấn để bắt đầu ghi âm</div>
                            <Button variant="success" size="lg" className="rounded-circle px-4 py-3" onClick={startRecording}>
                                🎤
                            </Button>
                        </>
                    )}

                    {recording && (
                        <>
                            <div className="text-danger fw-semibold mb-2">
                                ● Đang ghi âm — {formatTime(duration)}
                            </div>
                            <Button variant="danger" size="lg" onClick={stopRecording}>
                                ■ Dừng
                            </Button>
                        </>
                    )}

                    {!recording && audioBlob && (
                        <>
                            <div className="text-success mb-3">✓ Đã ghi xong — {formatTime(duration)}</div>
                            <audio controls src={audioUrl} className="w-100 mb-3" />
                            <div className="d-flex gap-2 justify-content-center">
                                <Button variant="outline-secondary" onClick={startRecording}>Ghi lại</Button>
                            </div>
                        </>
                    )}
                </Card.Body>
            </Card>

            {/* Transcript */}
            {transcript && (
                <Card className="border-0 bg-light mb-4">
                    <Card.Body>
                        <div className="small text-muted text-uppercase fw-semibold mb-2">Transcript nhận được</div>
                        <p className="mb-0" style={{ lineHeight: 1.8 }}>{transcript}</p>
                    </Card.Body>
                </Card>
            )}

            {/* Nộp */}
            {audioBlob && (
                <Button
                    variant="dark"
                    size="lg"
                    className="w-100"
                    disabled={submitting || !transcript.trim()}
                    onClick={submit}
                >
                    {submitting ? <><Spinner size="sm" className="me-2" />AI đang chấm...</> : "Nộp bài"}
                </Button>
            )}

            {audioBlob && !transcript.trim() && (
                <div className="text-center text-muted small mt-2">
                    Không nhận được transcript. Thử ghi lại hoặc kiểm tra microphone.
                </div>
            )}
        </Container>
    );
};

export default SpeakingPage;