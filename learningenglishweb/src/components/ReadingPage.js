import { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import { Button, Card, Form, Spinner } from "react-bootstrap";
import { authApis } from "./configs";

const ReadingPage = () => {
    const { lessonId } = useParams();

    const [lesson, setLesson] = useState(null);
    const [answers, setAnswers] = useState({});
    const [result, setResult] = useState(null);
    const [correctAnswers, setCorrectAnswers] = useState({});
    const [loading, setLoading] = useState(false);

    const passageRef = useRef();

    useEffect(() => {
        const load = async () => {
            const res = await authApis().get(`/api/lessons/${lessonId}`);
            setLesson(res.data);
        };
        load();
    }, [lessonId]);

    const choose = (id, value) => setAnswers(prev => ({ ...prev, [id]: value }));
    const handleInput = (id, value) => setAnswers(prev => ({ ...prev, [id]: value }));

    const submit = async () => {
        setLoading(true);
        try {
            const payload = { lessonId: lesson.id, answers };
            const res = await authApis().post("/api/reading/submit", payload);

            setResult(res.data);
            // Đảm bảo lấy được object correctAnswers từ Backend trả về
            setCorrectAnswers(res.data.correctAnswers || res.data.correct_answers || {});
        } catch (error) {
            console.error(error);
            alert("Lỗi khi nộp bài!");
        } finally {
            setLoading(false);
        }
    };

    if (!lesson) return <div className="text-center py-5"><Spinner /></div>;

    const passage = lesson.sections.find(s => s.type === "READING_PASSAGE");
    const questions = lesson.sections.filter(s => s.type !== "READING_PASSAGE");

    const scrollToTop = () => passageRef.current?.scrollIntoView({ behavior: "smooth" });

    return (
        <div className="container-fluid py-3">
            <h4 className="mb-3">{lesson.title}</h4>
            <div className="row">
                {/* LEFT: PASSAGE */}
                <div className="col-md-6" style={{ height: "80vh", overflowY: "auto" }}>
                    <Card ref={passageRef} className="p-3 bg-light border-0 shadow-sm">
                        <div style={{ whiteSpace: "pre-line", lineHeight: 1.8 }}>
                            {passage?.content}
                        </div>
                    </Card>
                </div>

                {/* RIGHT: QUESTIONS */}
                <div className="col-md-6" style={{ height: "80vh", overflowY: "auto" }}>
                    {questions.map((q, index) => {
                        const userAns = answers[q.id] || "";
                        // Fix lỗi map sai ID: Thử cả số và chuỗi
                        const correctAns = correctAnswers[q.id] || correctAnswers[String(q.id)] || "";

                        const isCorrect = result && userAns && correctAns &&
                            userAns.toLowerCase().trim() === correctAns.toLowerCase().trim();

                        return (
                            <Card key={q.id} className="mb-3 p-3 shadow-sm border-0">
                                <div className="d-flex justify-content-between mb-2">
                                    <b style={{ lineHeight: 1.6 }}>{index + 1}. {q.question}</b>
                                    <Button size="sm" variant="light" onClick={scrollToTop}>📖</Button>
                                </div>

                                {/* MULTIPLE CHOICE */}
                                {(q.type === "MULTIPLE_CHOICE" || q.type === "TRUE_FALSE_NG") && (
                                    <div className="mt-2">
                                        {q.options.map((opt, i) => {
                                            let variant = "outline-secondary";
                                            if (result) {
                                                if (opt === correctAns) variant = "success";
                                                else if (opt === userAns) variant = "danger";
                                            } else if (userAns === opt) {
                                                variant = "primary";
                                            }

                                            return (
                                                <Button
                                                    key={i}
                                                    className="d-block mb-2 text-start w-100"
                                                    variant={variant}
                                                    onClick={() => choose(q.id, opt)}
                                                    disabled={result != null}
                                                >
                                                    {opt}
                                                </Button>
                                            );
                                        })}
                                    </div>
                                )}

                                {/* FILL IN BLANK */}
                                {q.type === "FILL_IN_BLANK" && (
                                    <>
                                        <Form.Control
                                            className="mt-2 fw-semibold"
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
                            </Card>
                        );
                    })}

                    <Button onClick={submit} disabled={loading || result != null} variant="dark" size="lg" className="w-100 mb-3">
                        {loading ? <Spinner size="sm" /> : "Nộp bài"}
                    </Button>

                    {result && (
                        <div className="p-3 bg-success text-white rounded d-flex justify-content-between align-items-center mb-4">
                            <h5 className="mb-0">Score: {result.score} / {result.total}</h5>
                            <Button variant="light" size="sm" onClick={() => {
                                setResult(null);
                                setAnswers({});
                            }}>Làm lại</Button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ReadingPage;