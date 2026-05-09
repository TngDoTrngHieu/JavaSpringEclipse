import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Alert, Button, Card, Form, Spinner } from "react-bootstrap";
import { authApis } from "./configs";

const ListeningPage = () => {
    const { lessonId } = useParams();

    const [lesson, setLesson] = useState(null);
    const [answers, setAnswers] = useState({});
    const [result, setResult] = useState(null);
    const [correctAnswers, setCorrectAnswers] = useState({});

    const [submitting, setSubmitting] = useState(false);
    const [err, setErr] = useState("");

    useEffect(() => {
        const load = async () => {
            try {
                const res = await authApis().get(`/api/lessons/${lessonId}`);
                setLesson(res.data);
            } catch (error) {
                setErr("Không thể tải dữ liệu bài học.");
            }
        };
        load();
    }, [lessonId]);

    const choose = (sectionId, value) => setAnswers(prev => ({ ...prev, [sectionId]: value }));
    const handleInput = (sectionId, value) => setAnswers(prev => ({ ...prev, [sectionId]: value }));

    const submit = async () => {
        if (Object.keys(answers).length === 0) {
            setErr("Vui lòng trả lời ít nhất một câu hỏi trước khi nộp bài.");
            return;
        }

        try {
            setSubmitting(true);
            setErr("");
            setResult(null);

            const answersPayload = {};
            Object.entries(answers).forEach(([sectionId, value]) => {
                if (value != null && String(value).trim() !== "") {
                    answersPayload[String(sectionId)] = String(value).trim();
                }
            });

            const res = await authApis().post("/api/listening/submit", {
                lessonId: lesson.id,
                answers: answersPayload,
            });

            setResult(res.data);
            setCorrectAnswers(res.data.correctAnswers || res.data.correct_answers || {});
        } catch (error) {
            const errorMsg = error?.response?.data?.error || "Đã xảy ra lỗi khi nộp bài.";
            setErr(errorMsg);
        } finally {
            setSubmitting(false);
        }
    };

    if (!lesson && !err) return <div className="text-center py-5"><Spinner /></div>;

    const audioSection = lesson?.sections?.find(s => s.type === "LISTENING_AUDIO");
    let audioData = null;
    if (audioSection && audioSection.content) {
        try { audioData = JSON.parse(audioSection.content); } catch { }
    }
    const audioSrc = audioData?.audioUrl || audioData?.audio_url || "";
    const questions = lesson?.sections?.filter(s => s.type !== "LISTENING_AUDIO") || [];

    return (
        <div className="container py-4" style={{ maxWidth: 800 }}>
            <h3 className="mb-4">{lesson?.title}</h3>

            {err && <Alert variant="danger" className="mb-4">{err}</Alert>}

            {audioSrc && (
                <Card className="mb-4 shadow-sm border-0 bg-light">
                    <Card.Body>
                        <audio controls className="w-100" src={audioSrc} />
                    </Card.Body>
                </Card>
            )}

            {questions.map((q, index) => {
                const userAns = answers[q.id] || "";
                // Fix lỗi map sai ID: Thử cả số và chuỗi
                const correctAns = correctAnswers[q.id] || correctAnswers[String(q.id)] || "";

                const isCorrect = result && userAns && correctAns &&
                    userAns.toLowerCase().trim() === correctAns.toLowerCase().trim();

                return (
                    <Card key={q.id} className="mb-4 shadow-sm border-0">
                        <Card.Body>
                            <h6 className="mb-3" style={{ lineHeight: 1.6 }}>
                                <b>{index + 1}.</b> {q.question}
                            </h6>

                            {/* MULTIPLE CHOICE */}
                            {q.type === "MULTIPLE_CHOICE" && (
                                <div className="d-flex flex-column gap-2">
                                    {q.options.map((opt, i) => {
                                        const label = String.fromCharCode(65 + i);

                                        // 1. So sánh chuỗi an toàn
                                        const safeOpt = String(opt).trim().toLowerCase();
                                        const safeUserAns = String(userAns).trim().toLowerCase();
                                        const safeCorrectAns = String(correctAns).trim().toLowerCase();

                                        let variant = "outline-secondary";

                                        // 2. Logic tô màu
                                        if (result) {
                                            // NẾU ĐÃ NỘP BÀI:
                                            if (safeOpt === safeCorrectAns) {
                                                variant = "success"; // Đáp án đúng -> Màu xanh lá
                                            } else if (safeOpt === safeUserAns) {
                                                variant = "danger"; // User chọn sai -> Màu đỏ
                                            }
                                        } else if (safeOpt === safeUserAns) {
                                            // NẾU CHƯA NỘP BÀI:
                                            variant = "primary"; // Đang chọn -> Màu xanh dương
                                        }

                                        return (
                                            <Button
                                                key={i}
                                                variant={variant}
                                                className="text-start p-2"
                                                onClick={() => choose(q.id, opt)}
                                                disabled={result != null}
                                            >
                                                <span className="fw-bold me-2">{label}.</span> {opt}
                                            </Button>
                                        );
                                    })}
                                </div>
                            )}

                            {/* FILL IN BLANK */}
                            {q.type === "FILL_IN_BLANK" && (
                                <>
                                    <Form.Control
                                        type="text"
                                        size="lg"
                                        className="fw-semibold"
                                        placeholder="Nhập câu trả lời..."
                                        value={userAns}
                                        onChange={(e) => handleInput(q.id, e.target.value)}
                                        disabled={result != null}
                                        // ĐỔI MÀU Ô INPUT KHI CÓ KẾT QUẢ
                                        style={result ? {
                                            borderColor: isCorrect ? '#198754' : '#dc3545',
                                            backgroundColor: isCorrect ? '#d1e7dd' : '#f8d7da',
                                            color: isCorrect ? '#0f5132' : '#842029'
                                        } : {}}
                                    />

                                    {result && (
                                        <div className="mt-2">
                                            {isCorrect ? (
                                                <span className="text-success fw-bold">✔ Chính xác</span>
                                            ) : (
                                                <>
                                                    <span className="text-danger fw-bold">
                                                        ✖ Sai {userAns ? `(${userAns})` : "(Chưa trả lời)"}
                                                    </span>
                                                    <div className="text-success fw-bold mt-1">
                                                        Đáp án đúng: {correctAns || "Backend chưa trả về đáp án"}
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    )}
                                </>
                            )}
                        </Card.Body>
                    </Card>
                );
            })}

            <div className="d-flex align-items-center gap-3 mt-4 border-top pt-4">
                <Button variant="dark" size="lg" onClick={submit} disabled={submitting || result != null}>
                    {submitting ? <><Spinner as="span" animation="border" size="sm" className="me-2" /> Đang chấm bài...</> : "Nộp bài"}
                </Button>

                {result && (
                    <div className="ms-auto bg-success text-white px-4 py-2 rounded">
                        <h5 className="mb-0">Score: {result.score} / {result.total}</h5>
                    </div>
                )}
            </div>

            {result && (
                <Button variant="outline-secondary" className="mt-3 w-100" onClick={() => {
                    setResult(null);
                    setCorrectAnswers({});
                    setAnswers({});
                }}>
                    Làm lại bài này
                </Button>
            )}
        </div>
    );
};

export default ListeningPage;