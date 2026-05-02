import { useState, useRef, useEffect } from "react";
import axios from "axios";
import cookie from "react-cookies";
import { useNavigate } from "react-router-dom";
import { Toast, ToastContainer, Modal, Button } from "react-bootstrap";
import { authApis, endpoints } from "./configs";


export default function ChatBox() {
    const navigate = useNavigate();
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(false);

    const [toastInfo, setToastInfo] = useState({ show: false, msg: "", variant: "success" });
    const [showVipModal, setShowVipModal] = useState(false);

    const token = cookie.load("token");

    const [isVip, setIsVip] = useState(null);
    const [vipChecking, setVipChecking] = useState(false);
    const scrollRef = useRef(null);

    const showToast = (msg, variant = "danger") => {
        setToastInfo({ show: true, msg, variant });
    };

    useEffect(() => {
        if (!token) {
            setIsVip(false);
            return;
        }

        let mounted = true;
        setVipChecking(true);

        authApis()
            .get(endpoints.profile)
            .then((res) => {
                if (!mounted) return;
                setIsVip(res?.data?.isVip === true);
            })
            .catch(() => {
                if (!mounted) return;
                setIsVip(false);
            })
            .finally(() => mounted && setVipChecking(false));

        return () => {
            mounted = false;
        };
    }, [token]);

    const sendMessage = async () => {
        if (!message.trim()) return;
        if (!token) {
            showToast("Bạn cần đăng nhập để sử dụng AI Chat.");
            return;
        }

        if (isVip === false) {
            setShowVipModal(true);
            return;
        }

        if (isVip === null && vipChecking) {
            showToast("Đang xác minh quyền truy cập VIP, vui lòng chờ một chút...");
            return;
        }

        setMessages((prev) => [...prev, { role: "user", text: message }]);
        setMessage("");
        setLoading(true);

        try {
            const res = await authApis().post("/api/chat", { question: message });
            const answer = res?.data?.answer ?? "(không có trả lời)";
            setMessages((prev) => [...prev, { role: "bot", text: "" }]);
            typeText(answer, setMessages);
        } catch (err) {
            // If API blocks because of VIP, prompt upgrade
            if (err.response?.status === 403) {
                setShowVipModal(true);
                return;
            }

            setMessages((prev) => [...prev, { role: "bot", text: "Mình đang gặp sự cố kết nối. Bạn thử lại sau ít phút nhé." }]);
        } finally {
            setLoading(false);
        }
    };

    const saveWord = async (text) => {
        try {
            await axios.post(
                "/api/vocabularies/from-ai",
                { text },
                { headers: { Authorization: token ? `Bearer ${token}` : undefined } }
            );
        } catch (err) {
            throw err;
        }
    };

    const typeText = (text, setMessagesFn, speed = 20) => {
        let index = 0;
        const interval = setInterval(() => {
            setMessagesFn((prev) => {
                if (prev.length === 0) return prev;
                const last = prev[prev.length - 1];
                if (last.role !== "bot") return prev;

                const newText = text.slice(0, index + 1);
                return [...prev.slice(0, -1), { role: "bot", text: newText }];
            });
            index++;
            if (index >= text.length) clearInterval(interval);
        }, speed);
    };

    const isVocabResponse = (text) => {
        if (!text) return false;
        return text.includes("Word:") || text.includes("**Word:**");
    };

    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages]);

    const onKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    return (
        <div style={{ width: "100%", maxWidth: "1000px", margin: "20px auto", position: "relative" }}>
            <h2>Chat AI</h2>

            <div
                ref={scrollRef}
                style={{ border: "1px solid #ccc", height: "500px", overflowY: "auto", padding: "10px", background: "#fff" }}
            >
                {messages.map((m, i) => (
                    <div key={i} style={{ display: "flex", justifyContent: m.role === "user" ? "flex-end" : "flex-start", margin: "6px 0" }}>
                        <div style={{
                            textAlign: m.role === "user" ? "right" : "left",
                            background: m.role === "user" ? "#d1e7ff" : "#f1f1f1",
                            padding: "8px",
                            borderRadius: "8px",
                            display: "inline-block",
                            maxWidth: "80%"
                        }}>
                            <div style={{ fontSize: "12px", color: "#555", marginBottom: "4px" }}><b>{m.role === "user" ? "Bạn" : "AI"}:</b></div>

                            <div style={{ whiteSpace: "pre-line" }}>{m.text.replace(/\*\*/g, "")}</div>

                            {m.role === "bot" && isVocabResponse(m.text) && (
                                <div style={{ marginTop: "6px" }}>
                                    {m.saved ? (
                                        <span style={{ color: "#28a745", fontSize: "14px" }}> Đã lưu</span>
                                    ) : (
                                        <button
                                            style={{ padding: "4px 12px", cursor: "pointer", border: "1px solid #ccc", borderRadius: "4px" }}
                                            onClick={async (e) => {
                                                const btn = e.target;
                                                btn.innerText = "Đang lưu...";
                                                btn.disabled = true;

                                                try {
                                                    const cleanText = m.text.replace(/\*\*/g, "");
                                                    await saveWord(cleanText);

                                                    // 3. Gọi Toast báo thành công
                                                    setToastInfo({ show: true, msg: "Đã lưu từ vựng vào sổ tay của bạn.", variant: "success" });

                                                    setMessages((prev) => {
                                                        const newMsgs = [...prev];
                                                        newMsgs[i].saved = true;
                                                        return newMsgs;
                                                    });
                                                } catch (err) {
                                                    btn.innerText = "Lưu";
                                                    btn.disabled = false;
                                                    // Gọi Toast báo lỗi
                                                    setToastInfo({ show: true, msg: "Không thể lưu từ vựng lúc này. Vui lòng thử lại sau.", variant: "danger" });
                                                }
                                            }}
                                        >
                                            Lưu
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                ))}
                {loading && <p style={{ textAlign: "center", color: "#666" }}>AI đang trả lời...</p>}
            </div>

            <div style={{ marginTop: "8px", display: "flex", gap: "8px" }}>
                <textarea
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={onKeyDown}
                    placeholder="Nhập từ... (Shift+Enter xuống dòng)"
                    style={{ flex: 1, padding: "8px", resize: "vertical", minHeight: "40px" }}
                />

                <button onClick={sendMessage} style={{ padding: "8px 12px" }} disabled={loading}>
                    {loading ? "Đang..." : "Gửi"}
                </button>
            </div>

            <ToastContainer position="top-end" className="p-3" style={{ position: "fixed", zIndex: 9999 }}>
                <Toast
                    show={toastInfo.show}
                    delay={2500}
                    autohide
                    onClose={() => setToastInfo({ ...toastInfo, show: false })}
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
                    AI Chat hiện là đặc quyền VIP. Nâng cấp tài khoản để mở khóa tính năng này.
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
}