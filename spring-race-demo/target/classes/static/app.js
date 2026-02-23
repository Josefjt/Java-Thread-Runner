const racersInput = document.getElementById("racersInput");
const newRaceBtn = document.getElementById("newRaceBtn");
const startBtn = document.getElementById("startBtn");
const restartBtn = document.getElementById("restartBtn");
const raceIdEl = document.getElementById("raceId");
const raceStatusEl = document.getElementById("raceStatus");
const winnerLabelEl = document.getElementById("winnerLabel");
const messageEl = document.getElementById("message");
const trackAreaEl = document.getElementById("trackArea");
const desktopEl = document.querySelector(".desktop");
const raceWindowEl = document.getElementById("raceWindow");
const aboutWindowEl = document.getElementById("aboutWindow");
const aboutMenuItemEl = document.getElementById("aboutMenuItem");
const backToRaceBtn = document.getElementById("backToRaceBtn");
const aboutSourceCodeEl = document.getElementById("aboutSourceCode");
const showOriginalImageBtn = document.getElementById("showOriginalImageBtn");
const originalImagePopupEl = document.getElementById("originalImagePopup");
const closeOriginalImageBtn = document.getElementById("closeOriginalImageBtn");
const closeBtn = document.getElementById("closeBtn");
const closeConfirmEl = document.getElementById("closeConfirm");
const closeYesBtn = document.getElementById("closeYesBtn");
const closeNoBtn = document.getElementById("closeNoBtn");

let raceId = null;
let pollTimer = null;
let closePromptOpen = false;
let aboutSourceLoaded = false;

function setMessage(text, isOk = false) {
  messageEl.textContent = text || "";
  messageEl.classList.toggle("ok", Boolean(isOk));
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    method: options.method || "GET",
    headers: {
      "Content-Type": "application/json"
    },
    ...options
  });

  if (!response.ok) {
    let errorMessage = `Request failed (${response.status})`;
    try {
      const errorBody = await response.json();
      if (errorBody.message) {
        errorMessage = errorBody.message;
      }
    } catch (error) {
      // Ignore JSON parse errors and keep fallback message.
    }
    throw new Error(errorMessage);
  }

  return response.json();
}

function renderRace(race) {
  raceIdEl.textContent = race.id;
  raceStatusEl.textContent = race.status;
  winnerLabelEl.textContent = race.winnerNumber ? `#${race.winnerNumber}` : "-";

  startBtn.disabled = race.status !== "READY";
  restartBtn.disabled = false;

  const finishLine = race.finishLine; // setting finish line based on width of image
  const rowWidth = finishLine + 70;
  trackAreaEl.innerHTML = "";
  trackAreaEl.style.width = `${rowWidth + 24}px`;

  race.racers.forEach((racer) => {
    const track = document.createElement("div");
    track.className = "track";
    track.style.width = `${rowWidth}px`;

    const label = document.createElement("div");
    label.className = "racer-label";
    label.textContent = `Racer #${racer.number}`;

    const finish = document.createElement("div");
    finish.className = "finish-line";
    finish.style.left = `${finishLine}px`;

    const racerEl = document.createElement("div");
    racerEl.className = "racer";
    racerEl.style.left = `${Math.min(racer.position, finishLine)}px`; // updates position of Threads.

    const racerImg = document.createElement("img");
    racerImg.src = "/races.gif";
    racerImg.alt = `Racer #${racer.number}`;
    racerEl.appendChild(racerImg);

    track.appendChild(label);
    track.appendChild(finish);
    track.appendChild(racerEl);
    trackAreaEl.appendChild(track);
  });

  if (race.status === "FINISHED" && race.winnerNumber) {
    const banner = document.createElement("div");
    banner.className = "winner-banner";
    banner.textContent = `Winner is racer #${race.winnerNumber}`;
    trackAreaEl.appendChild(banner);
  }
}

async function refreshRace() {
  if (!raceId) {
    return;
  }

  try {
    const race = await api(`/api/races/${raceId}`);
    renderRace(race);
  } catch (error) {
    setMessage(error.message);
    stopPolling();
  }
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
}

function startPolling() {
  stopPolling();
  pollTimer = setInterval(refreshRace, 100);
}

async function loadAboutSource() {
  if (aboutSourceLoaded || !aboutSourceCodeEl) {
    return;
  }

  try {
    const response = await fetch("/original/Races.java.txt", { cache: "no-store" });
    if (!response.ok) {
      throw new Error(`Unable to load source (${response.status})`);
    }
    const codeText = await response.text();
    aboutSourceCodeEl.textContent = codeText;
    aboutSourceLoaded = true;
  } catch (error) {
    aboutSourceCodeEl.textContent = `Unable to load original source.\n\n${error.message}`;
  }
}

function showAboutPage() {
  if (!raceWindowEl || !aboutWindowEl || closePromptOpen) {
    return;
  }
  stopPolling();
  raceWindowEl.classList.add("is-hidden");
  aboutWindowEl.classList.remove("is-hidden");
  void loadAboutSource();
}

function showRacePage() {
  if (!raceWindowEl || !aboutWindowEl) {
    return;
  }
  closeOriginalImagePopup();
  aboutWindowEl.classList.add("is-hidden");
  raceWindowEl.classList.remove("is-hidden");
  if (!desktopEl || !desktopEl.classList.contains("closed-confirmed")) {
    startPolling();
  }
}

function openOriginalImagePopup() {
  if (!originalImagePopupEl) {
    return;
  }
  originalImagePopupEl.classList.remove("hidden");
  originalImagePopupEl.setAttribute("aria-hidden", "false");
}

function closeOriginalImagePopup() {
  if (!originalImagePopupEl) {
    return;
  }
  originalImagePopupEl.classList.add("hidden");
  originalImagePopupEl.setAttribute("aria-hidden", "true");
}

function openClosePrompt() {
  if (closePromptOpen || !raceWindowEl || !closeConfirmEl) {
    return;
  }

  closePromptOpen = true;
  stopPolling();
  closeConfirmEl.classList.remove("hidden");
  closeConfirmEl.setAttribute("aria-hidden", "false");
}

function cancelClosePrompt() {
  if (!closePromptOpen || !raceWindowEl || !closeConfirmEl) {
    return;
  }

  closePromptOpen = false;
  raceWindowEl.classList.remove("is-hidden");
  closeConfirmEl.classList.add("hidden");
  closeConfirmEl.setAttribute("aria-hidden", "true");
  if (!aboutWindowEl || aboutWindowEl.classList.contains("is-hidden")) {
    startPolling();
  }
}

function confirmClosePrompt() {
  if (!closePromptOpen || !closeConfirmEl || !raceWindowEl) {
    return;
  }

  closePromptOpen = false;
  closeConfirmEl.classList.add("hidden");
  closeConfirmEl.setAttribute("aria-hidden", "true");
  stopPolling();
  raceWindowEl.classList.add("is-hidden");
  if (aboutWindowEl) {
    aboutWindowEl.classList.add("is-hidden");
  }
  closeOriginalImagePopup();
  if (desktopEl) {
    desktopEl.classList.add("closed-confirmed");
  }
}

async function createRace() {
  const racers = Number(racersInput.value);
  setMessage("");
  try {
    const race = await api(`/api/races?racers=${encodeURIComponent(racers)}`, {
      method: "POST"
    });
    raceId = race.id;
    renderRace(race);
    startPolling();
    setMessage(`Created race ${race.id}`, true);
  } catch (error) {
    setMessage(error.message);
  }
}

async function startRace() {
  if (!raceId) {
    setMessage("Create a race first.");
    return;
  }

  setMessage("");
  try {
    const race = await api(`/api/races/${raceId}/start`, { method: "POST" });
    renderRace(race);
    setMessage("Race started.", true);
  } catch (error) {
    setMessage(error.message);
  }
}

async function restartRace() {
  if (!raceId) {
    setMessage("Create a race first.");
    return;
  }

  setMessage("");
  try {
    const race = await api(`/api/races/${raceId}/restart`, { method: "POST" });
    renderRace(race);
    setMessage("Race reset. Press Start to run again.", true);
  } catch (error) {
    setMessage(error.message);
  }
}

newRaceBtn.addEventListener("click", createRace);
startBtn.addEventListener("click", startRace);
restartBtn.addEventListener("click", restartRace);
if (closeBtn) {
  closeBtn.addEventListener("click", openClosePrompt);
}
if (closeNoBtn) {
  closeNoBtn.addEventListener("click", cancelClosePrompt);
}
if (closeYesBtn) {
  closeYesBtn.addEventListener("click", confirmClosePrompt);
}
if (aboutMenuItemEl) {
  aboutMenuItemEl.addEventListener("click", showAboutPage);
}
if (backToRaceBtn) {
  backToRaceBtn.addEventListener("click", showRacePage);
}
if (showOriginalImageBtn) {
  showOriginalImageBtn.addEventListener("click", openOriginalImagePopup);
}
if (closeOriginalImageBtn) {
  closeOriginalImageBtn.addEventListener("click", closeOriginalImagePopup);
}
if (originalImagePopupEl) {
  originalImagePopupEl.addEventListener("click", (event) => {
    if (event.target === originalImagePopupEl) {
      closeOriginalImagePopup();
    }
  });
}

window.addEventListener("beforeunload", stopPolling);

createRace();
