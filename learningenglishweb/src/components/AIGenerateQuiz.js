import React, { useState, useEffect } from "react";
import { Button, Form, Spinner, Card, ProgressBar, Toast, ToastContainer, Modal } from "react-bootstrap";
import axios from "axios";
import cookie from "react-cookies";
import { useNavigate } from "react-router-dom";

const AIGenerateQuiz = () => {
    const navigate = useNavigate();
    const [passage, setPassage] = useState("");
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [toastInfo, setToastInfo] = useState({ show: false, msg: "", variant: "danger" });
    const [showVipModal, setShowVipModal] = useState(false);

    const [currentStep, setCurrentStep] = useState(0);
    const [userAnswers, setUserAnswers] = useState({});
    const [quizFinished, setQuizFinished] = useState(false);

    const showToast = (msg, variant = "danger") => {
        setToastInfo({ show: true, msg, variant });
    };

    const generate = async () => {
        setResult(null);
        setQuizFinished(false);
        setCurrentStep(0);
        setUserAnswers({});

        if (!passage || passage.trim().length === 0) {
            showToast("Bạn chưa nhập đoạn văn. Hãy thêm nội dung để hệ thống tạo câu hỏi.");
            return;
        }
        // Check login
        const token = cookie.load("token");
        if (!token) {
            showToast("Bạn cần đăng nhập để sử dụng tính năng tạo câu hỏi AI.");
            return;
        }

        // Check VIP cached state
        if (isVip === false) {
            setShowVipModal(true);
            return;
        }

        if (isVip === null && vipChecking) {
            showToast("Đang xác minh quyền truy cập VIP. Vui lòng thử lại sau vài giây.");
            return;
        }

        setLoading(true);
        try {
            const resp = await axios.post(
                "/api/ai/generate-quiz",
                { passage },
                { headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` } }
            );

            const data = resp.data;
            if (Array.isArray(data)) {
                setResult(data);
            } else if (data.raw) {
                showToast("Hệ thống đã tạo phản hồi nhưng chưa đúng định dạng. Vui lòng thử lại.");
            } else {
                showToast("Phản hồi không hợp lệ. Vui lòng thử lại.");
            }
        } catch (err) {
            if (err.response?.status === 403) {
                setShowVipModal(true);
                return;
            }
            showToast("Không thể kết nối tới hệ thống AI. Vui lòng thử lại sau.");
        } finally {
            setLoading(false);
        }
    };

    // VIP state and check
    const [isVip, setIsVip] = useState(null);
    const [vipChecking, setVipChecking] = useState(false);

    useEffect(() => {
        const token = cookie.load("token");
        if (!token) {
            setIsVip(false);
            return;
        }

        let mounted = true;
        setVipChecking(true);
        axios
            .get("/api/user/is-vip", { headers: { Authorization: `Bearer ${token}` } })
            .then((res) => mounted && setIsVip(res?.data?.vip === true))
            .catch(() => mounted && setIsVip(false))
            .finally(() => mounted && setVipChecking(false));

        return () => (mounted = false);
    }, []);

    // Hàm tính điểm an toàn (Sử dụng Optional Chaining)
    const calculateScore = () => {
        if (!result) return 0;
        return result.reduce((score, q, idx) => {
            return userAnswers[idx] === q.correctAnswer ? score + 1 : score;
        }, 0);
    };

    // Hàm reset an toàn
    const handleReset = () => {
        setResult(null);
        setPassage("");
        setQuizFinished(false);
    };

    return (
        <div className="container py-4">
            <h4>Hệ thống luyện tập Reading</h4>

            {/* MÀN HÌNH NHẬP LIỆU */}
            {!result && !loading && (
                <Card className="shadow-sm">
                    <Card.Body>
                        <Form.Group className="mb-3">
                            <Form.Control
                                as="textarea"
                                rows={10}
                                value={passage}
                                onChange={(e) => setPassage(e.target.value)}
                                placeholder="Dán đoạn văn tiếng Anh tại đây..."
                            />
                        </Form.Group>
                        <Button variant="primary" className="w-100" onClick={generate}>
                            Bắt đầu tạo câu hỏi
                        </Button>
                    </Card.Body>
                </Card>
            )}

            {loading && (
                <div className="text-center my-5">
                    <Spinner animation="border" variant="primary" />
                    <p className="mt-2">Đang khởi tạo câu hỏi...</p>
                </div>
            )}

            {/* GIAO DIỆN LÀM BÀI - Đã bọc kiểm tra null chặt chẽ */}
            {result && Array.isArray(result) && result.length > 0 && !quizFinished && (
                <Card className="shadow">
                    <Card.Header>
                        {/* Sử dụng ?. để tránh lỗi length khi result bị null bất ngờ */}
                        <ProgressBar
                            now={((currentStep + 1) / (result?.length || 1)) * 100}
                            label={`${currentStep + 1}/${result?.length || 0}`}
                        />
                    </Card.Header>
                    <Card.Body>
                        <h5>{result[currentStep]?.question}</h5>
                        <div className="d-grid gap-2 mt-3">
                            {result[currentStep]?.options?.map((op, i) => (
                                <Button
                                    key={i}
                                    variant={userAnswers[currentStep] === op ? "primary" : "outline-dark"}
                                    onClick={() => setUserAnswers({ ...userAnswers, [currentStep]: op })}
                                    className="text-start py-2"
                                >
                                    <strong>{String.fromCharCode(65 + i)}.</strong> {op}
                                </Button>
                            ))}
                        </div>
                    </Card.Body>
                    <Card.Footer className="d-flex justify-content-between">
                        <Button variant="secondary" disabled={currentStep === 0} onClick={() => setCurrentStep(currentStep - 1)}>Câu trước</Button>
                        <Button
                            variant="success"
                            disabled={!userAnswers[currentStep]}
                            onClick={() => (currentStep < (result?.length || 0) - 1) ? setCurrentStep(currentStep + 1) : setQuizFinished(true)}
                        >
                            {currentStep === (result?.length || 0) - 1 ? "Xem kết quả" : "Câu tiếp theo"}
                        </Button>
                    </Card.Footer>
                </Card>
            )}

            {/* MÀN HÌNH KẾT QUẢ */}
            {quizFinished && (
                <Card className="shadow">
                    <Card.Body>
                        <div className="text-center mb-4">
                            <h2 className="text-success fw-bold">Kết quả: {calculateScore()} / {result?.length || 0}</h2>
                        </div>

                        {/* Danh sách xem lại đáp án */}
                        <div className="mt-4">
                            <h5 className="mb-3 fw-bold border-bottom pb-2">Chi tiết bài làm:</h5>
                            {result.map((q, idx) => {
                                const userAnswer = userAnswers[idx];
                                const isCorrect = userAnswer === q.correctAnswer;

                                return (
                                    <Card key={idx} className="mb-4 border-0 bg-light">
                                        <Card.Body>
                                            <h6 className="fw-bold mb-3">Câu {idx + 1}: {q.question}</h6>

                                            {/* Hiện hết tất cả 4 đáp án A, B, C, D */}
                                            <div className="d-flex flex-column gap-2 mb-3">
                                                {q.options?.map((op, i) => {
                                                    // Logic tô màu đáp án
                                                    let bgClass = "bg-white border";
                                                    let textClass = "text-dark";

                                                    if (op === userAnswer && isCorrect) {
                                                        // Chọn đúng -> Nền xanh lá
                                                        bgClass = "bg-success border-success";
                                                        textClass = "text-white";
                                                    } else if (op === userAnswer && !isCorrect) {
                                                        // Chọn sai -> Nền đỏ
                                                        bgClass = "bg-danger border-danger";
                                                        textClass = "text-white";
                                                    } else if (op === q.correctAnswer) {
                                                        // Đáp án đúng (mà người dùng không chọn) -> Viền xanh chữ xanh
                                                        bgClass = "bg-white border-success";
                                                        textClass = "text-success fw-bold";
                                                    }

                                                    return (
                                                        <div key={i} className={`p-2 rounded ${bgClass} ${textClass}`}>
                                                            <strong>{String.fromCharCode(65 + i)}.</strong> {op}
                                                        </div>
                                                    );
                                                })}
                                            </div>


                                            <div className="pt-3 border-top">
                                                <p className="mb-1">
                                                    <strong>Bạn chọn: </strong>
                                                    <span className={isCorrect ? "text-success fw-bold" : "text-danger fw-bold"}>
                                                        {userAnswer || "Chưa trả lời"}
                                                    </span>
                                                    {isCorrect ? " ✅" : " ❌"}
                                                </p>
                                                {!isCorrect && (
                                                    <p className="mb-0">
                                                        <strong>Đáp án đúng: </strong>
                                                        <span className="text-success fw-bold">{q.correctAnswer}</span>
                                                    </p>
                                                )}
                                            </div>
                                        </Card.Body>
                                    </Card>
                                );
                            })}
                        </div>

                        <div className="text-center mt-4">
                            <Button variant="primary" size="lg" onClick={handleReset}>Làm đoạn văn khác</Button>
                        </div>
                    </Card.Body>
                </Card>
            )}

            <ToastContainer position="top-end" className="p-3" style={{ position: "fixed", zIndex: 9999 }}>
                <Toast
                    show={toastInfo.show}
                    delay={2500}
                    autohide
                    onClose={() => setToastInfo((prev) => ({ ...prev, show: false }))}
                >
                    <Toast.Body style={{ color: toastInfo.variant === "success" ? "#28a745" : "#dc3545" }}>
                        {toastInfo.msg}
                    </Toast.Body>
                </Toast>
            </ToastContainer>

            <Modal show={showVipModal} onHide={() => setShowVipModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Mở rộng quyền truy cập</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Tính năng tạo câu hỏi AI hiện là đặc quyền VIP. Nâng cấp để mở khóa ngay bây giờ.
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowVipModal(false)}>
                        Để sau
                    </Button>
                    <Button
                        variant="primary"
                        onClick={() => {
                            setShowVipModal(false);
                            navigate("/upgrade-vip");
                        }}
                    >
                        Nâng cấp ngay
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default AIGenerateQuiz;