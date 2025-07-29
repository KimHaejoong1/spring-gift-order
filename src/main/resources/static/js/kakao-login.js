window.onload = function () {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get("code");

  if (code) {
    const authCodeElement = document.getElementById("authCode");
    authCodeElement.textContent = code;
    authCodeElement.style.cursor = "pointer";
    authCodeElement.title = "클릭하여 복사";

    authCodeElement.addEventListener("click", function () {
      copyToClipboard(code);
    });

    document.getElementById("codeResult").style.display = "block";
    window.currentCode = code;
  }
};

function copyToClipboard(text) {
  navigator.clipboard
    .writeText(text)
    .then(() => {
      showCopySuccess();
    })
    .catch((err) => {
      console.error("클립보드 복사 실패:", err);
      alert("클립보드 복사에 실패했습니다.");
    });
}

function showCopySuccess() {
  const notification = document.createElement("div");
  notification.className = "copy-notification";
  notification.textContent = "인가 코드가 복사되었습니다!";

  document.body.appendChild(notification);

  setTimeout(() => {
    notification.classList.add("show");
  }, 10);

  setTimeout(() => {
    notification.classList.remove("show");
    setTimeout(() => {
      document.body.removeChild(notification);
    }, 300);
  }, 2000);
}
