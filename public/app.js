(() => {
  const STEPS = [
    { id: 'input', num: 1, label: 'Expressions' },
    { id: 'scene', num: 2, label: 'Scene' },
    { id: 'storyboard', num: 3, label: 'Storyboard' },
    { id: 'video', num: 4, label: 'Movie' },
    { id: 'quiz', num: 5, label: 'Quiz' },
    { id: 'final', num: 6, label: 'Finish' },
  ];

  const DEFAULT_EXPRESSIONS = [
    { id: 'e1', phrase: 'was going to', korean: '~하려고 했었다', movie: 'Notting Hill' },
    { id: 'e2', phrase: 'If S had ..., S could ...', korean: '~라면 ...할 수 있을 텐데', movie: 'Minari' },
    { id: 'e3', phrase: "I'm really bummed", korean: '기분이 안 좋아', movie: 'Inside Out 2' },
    { id: 'e4', phrase: "I'm over the moon", korean: '뛸 듯이 기뻐', movie: 'Inside Out 2' },
    { id: 'e5', phrase: 'low point', korean: '최악의 순간', movie: 'Notting Hill' },
    { id: 'e6', phrase: 'make fun of', korean: '~를 놀리다', movie: 'Inside Out 2' },
    { id: 'e7', phrase: 'crossed the line', korean: '선을 넘다', movie: 'Inside Out 2' },
    { id: 'e8', phrase: 'jerk', korean: '못된 녀석', movie: 'Inside Out 2' },
    { id: 'e9', phrase: 'belong in jail', korean: '감옥에 가야 한다', movie: 'Notting Hill' },
  ];

  const DEFAULT_SCENE = {
    place: 'A bright, sunny beach with blue ocean waves and golden sand.',
    characters:
      'Friend A (young adult male, short brown hair, yellow t-shirt and red swim shorts), Friend B (young adult male, black curly hair, blue tank top and denim shorts), a cute cartoonish brown poop character with tiny smiling eyes on the sand, a middle-aged passerby man in a Hawaiian shirt, and a crowd of beachgoers.',
    situation:
      'Walking on the beach talking, stepping on cute poop, getting teased, throwing poop back and forth, hitting a passerby, and escalating into a chaotic beach-wide poop fight.',
    style: '3D animation style',
    styleOptions: [
      '3D animation style',
      '2D Pastel Storybook',
      'Claymation Comedy',
      'Anime Summer Slice-of-Life',
    ],
  };

  const DEFAULT_BEATS = [
    {
      id: 1,
      title: 'Beat 1: Hook',
      timeRange: '0-7s',
      startSec: 0,
      endSec: 7,
      image: './assets/images/img_beat_1.jpg',
      scene:
        'Bright 3D animated sunny beach. Friend A and Friend B walk along the shore smiling and chatting. Ahead on the sand sits a cute, cartoon poop character.',
      lines: [
        {
          speaker: 'Friend A',
          english: 'Nice day! I **was going to** stay home.',
          korean: 'A: 날씨 좋다! 집에 있으려고 했는데.',
        },
        {
          speaker: 'Friend B',
          english: '**If we had time**, we **could** walk further.',
          korean: 'B: 그러게! 시간이 있다면 더 걸을 텐데.',
        },
      ],
    },
    {
      id: 2,
      title: 'Beat 2: Problem',
      timeRange: '7-15s',
      startSec: 7,
      endSec: 15,
      image: './assets/images/img_beat_2.jpg',
      scene:
        'Friend A steps squarely onto the cute poop. Friend A looks down in disgust and gets bummed out. Friend B points at his shoe and laughs hysterically.',
      lines: [
        {
          speaker: 'Friend A',
          english: "Eww! I stepped in poop! **I'm really bummed**!",
          korean: 'A: 으악! 나 똥 밟았어! 기분 진짜 안 좋아졌어!',
        },
        {
          speaker: 'Friend B',
          english: "Haha! **I'm over the moon**!",
          korean: 'B: 하하! 나 너무 신나!',
        },
      ],
    },
    {
      id: 3,
      title: 'Beat 3: Turn',
      timeRange: '15-23s',
      startSec: 15,
      endSec: 23,
      image: './assets/images/img_beat_3.jpg',
      scene:
        'Furious Friend A scoops up the cute poop and throws it at Friend B. Friend B is shocked, gets hit, turns dead serious, and angrily hurls poop back at Friend A.',
      lines: [
        {
          speaker: 'Friend A',
          english: 'This is the real **low point**! You **make fun of** me?! Take this!',
          korean: 'A: 오늘 진짜 최악의 순간이네! 감히 날 보고 놀려?! 이거나 먹어라!',
        },
        {
          speaker: 'Friend B',
          english: 'Gah! You threw poop at me?! You **crossed the line**!',
          korean: 'B: 악! 너 나한테 지금 똥을 던진 거야?! 너 완전 선 넘었어!',
        },
      ],
    },
    {
      id: 4,
      title: 'Beat 4: Close',
      timeRange: '23-30s',
      startSec: 23,
      endSec: 30,
      image: './assets/images/img_beat_4.jpg',
      scene:
        'A flying piece of poop misses and splatters onto a nearby middle-aged man in a Hawaiian shirt. He gets outraged and throws it back. Soon, multiple beachgoers start throwing poop at each other in a wild, funny beach fight.',
      lines: [
        {
          speaker: 'Passerby',
          english: 'Gah! What is this?! You **jerk**s! You **belong in jail**!',
          korean: 'Passerby: 악! 이게 뭐야?! 저 자식들이! 너네 감옥이나 가라!',
        },
      ],
    },
  ];

  const QUIZ_QUESTIONS = [
    {
      id: 1,
      question: 'What expression did Friend A use to describe his feeling after stepping in poop?',
      options: [
        "A) I'm over the moon",
        "B) I'm really bummed",
        'C) Low point',
        'D) Read the room',
      ],
      correctIndex: 1,
      explanation: '"I\'m really bummed" (Inside Out 2) means feeling disappointed or down!',
    },
    {
      id: 2,
      question: 'What expression did Friend B use when laughing at Friend A stepping in poop?',
      options: [
        "A) I'm really bummed",
        'B) Crossed the line',
        "C) I'm over the moon",
        'D) Belong in jail',
      ],
      correctIndex: 2,
      explanation: '"I\'m over the moon" (Inside Out 2) means feeling thrilled or super happy!',
    },
  ];

  const state = {
    step: 'input',
    expressions: structuredClone(DEFAULT_EXPRESSIONS),
    scene: structuredClone(DEFAULT_SCENE),
    beats: structuredClone(DEFAULT_BEATS),
    subtitlesEnabled: true,
    quizSelections: {},
    video: {
      hasStarted: false,
      isPlaying: false,
      elapsedSec: 0,
      lastSpokenBeatId: -1,
      isFullScreen: false,
      subtitleBeatId: 1,
      subtitleSpeaker: '',
      subtitleEmotion: '',
      subtitleText: '',
      spokenWords: 0,
    },
    exportStatus: null,
  };

  let rafTimer = null;
  let wordFallbackTimer = null;

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function formatBoldExpressions(text) {
    const escaped = escapeHtml(text);
    return escaped.replace(/\*\*(.+?)\*\*/g, '<span class="hl-expr">$1</span>');
  }

  function countTotalWords(beats) {
    return beats
      .flatMap((b) => b.lines)
      .flatMap((l) =>
        l.english
          .replace(/\*\*/g, '')
          .trim()
          .split(/\s+/)
          .filter(Boolean)
      ).length;
  }

  function getValidationWarning() {
    const count = state.expressions.length;
    const movies = new Set(
      state.expressions.map((e) => e.movie.trim().toLowerCase()).filter(Boolean)
    ).size;
    if (count < 5 && movies <= 1) {
      return `Heads up: Fewer than 5 expressions (${count}) and only ${movies} movie. Add at least 5 expressions from 2+ movies!`;
    }
    if (count < 5) {
      return `Heads up: You have ${count} expressions. Add at least 5 expressions for a complete 30s story!`;
    }
    if (movies <= 1) {
      return `Heads up: Only ${movies} movie is represented. Mix expressions from 2 or more movies!`;
    }
    return null;
  }

  // --- Expressive Male Voice Acting (Web Speech API) ---
  function pickMaleVoices() {
    if (!('speechSynthesis' in window)) return { a: null, b: null, p: null };
    const voices = window.speechSynthesis.getVoices() || [];
    const enVoices = voices.filter((v) => v.lang && v.lang.toLowerCase().startsWith('en'));
    const maleHints = ['male', 'david', 'mark', 'guy', 'andrew', 'brian', 'christopher', 'roger', 'daniel', 'alex', 'fred', 'aaron', 'arthur', 'google uk english male'];
    const femaleHints = ['female', 'zira', 'jenny', 'aria', 'samantha', 'victoria', 'karen', 'google us english'];
    const males = enVoices.filter((v) => {
      const n = v.name.toLowerCase();
      return maleHints.some((h) => n.includes(h)) && !femaleHints.some((f) => n.includes(f));
    });
    const pool = males.length ? males : enVoices;
    return {
      a: pool[0] || null,
      b: pool[1] || pool[0] || null,
      p: pool[2] || pool[pool.length - 1] || null,
    };
  }

  function getVoiceDelivery(beatId, speaker) {
    const voices = pickMaleVoices();
    if (speaker.toLowerCase().includes('friend a')) {
      if (beatId === 1) return { voice: voices.a, pitch: 0.95, rate: 0.98, emotion: 'Cheerful & Relaxed' };
      if (beatId === 2) return { voice: voices.a, pitch: 0.78, rate: 0.85, emotion: 'Disgusted & Bummed' };
      return { voice: voices.a, pitch: 1.08, rate: 1.18, emotion: 'Angrily Shouting' };
    }
    if (speaker.toLowerCase().includes('friend b')) {
      if (beatId === 1) return { voice: voices.b, pitch: 1.04, rate: 1.02, emotion: 'Cheerful' };
      if (beatId === 2) return { voice: voices.b, pitch: 1.18, rate: 1.14, emotion: 'Laughing Hysterically' };
      return { voice: voices.b, pitch: 1.12, rate: 1.22, emotion: 'Shocked & Furious' };
    }
    return { voice: voices.p, pitch: 0.65, rate: 1.06, emotion: 'Deep & Outraged' };
  }

  function stopAudio() {
    clearInterval(wordFallbackTimer);
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
  }

  function speakBeatLines(beat) {
    stopAudio();
    if (!beat || !beat.lines.length) return;
    let idx = 0;

    const speakNext = () => {
      if (!state.video.isPlaying || idx >= beat.lines.length) return;
      const line = beat.lines[idx];
      const clean = line.english.replace(/\*\*/g, '').trim();
      const words = clean.split(/\s+/).filter(Boolean);
      const delivery = getVoiceDelivery(beat.id, line.speaker);

      state.video.subtitleBeatId = beat.id;
      state.video.subtitleSpeaker = line.speaker;
      state.video.subtitleEmotion = delivery.emotion;
      state.video.subtitleText = clean;
      state.video.spokenWords = 1;
      updateLiveVideoDom();

      clearInterval(wordFallbackTimer);
      let w = 1;
      wordFallbackTimer = setInterval(() => {
        if (!state.video.isPlaying) return;
        if (w < words.length) {
          w++;
          if (state.video.spokenWords < w) {
            state.video.spokenWords = w;
            updateLiveVideoDom();
          }
        }
      }, 290);

      if ('speechSynthesis' in window) {
        const utter = new SpeechSynthesisUtterance(clean);
        utter.lang = 'en-US';
        if (delivery.voice) utter.voice = delivery.voice;
        utter.pitch = delivery.pitch;
        utter.rate = delivery.rate;
        utter.onboundary = (e) => {
          if (typeof e.charIndex === 'number') {
            const prefix = clean.slice(0, e.charIndex + 1).trim();
            const count = prefix ? prefix.split(/\s+/).length : 1;
            state.video.spokenWords = Math.max(state.video.spokenWords, Math.min(count, words.length));
            updateLiveVideoDom();
          }
        };
        utter.onend = () => {
          clearInterval(wordFallbackTimer);
          idx++;
          if (idx < beat.lines.length && state.video.isPlaying) {
            speakNext();
          }
        };
        window.speechSynthesis.speak(utter);
      } else {
        setTimeout(() => {
          idx++;
          if (idx < beat.lines.length && state.video.isPlaying) speakNext();
        }, words.length * 320);
      }
    };

    speakNext();
  }

  function getActiveBeat(sec) {
    return state.beats.find((b) => sec >= b.startSec && sec < b.endSec) || state.beats[state.beats.length - 1];
  }

  function startVideoTicker() {
    clearInterval(rafTimer);
    let last = performance.now();
    rafTimer = setInterval(() => {
      if (!state.video.isPlaying) return;
      const now = performance.now();
      const dt = Math.min(0.1, (now - last) / 1000);
      last = now;
      state.video.elapsedSec = Math.min(30, state.video.elapsedSec + dt);

      const beat = getActiveBeat(state.video.elapsedSec);
      if (state.video.lastSpokenBeatId !== beat.id) {
        state.video.lastSpokenBeatId = beat.id;
        speakBeatLines(beat);
      }

      if (state.video.elapsedSec >= 30) {
        pauseVideo();
        exitFullScreen();
        openQuizPopup();
      } else {
        updateLiveVideoDom();
      }
    }, 50);
  }

  function playOrResumeVideo() {
    if (state.video.elapsedSec >= 30) {
      state.video.elapsedSec = 0;
      state.video.lastSpokenBeatId = -1;
    }
    state.video.hasStarted = true;
    state.video.isPlaying = true;
    const beat = getActiveBeat(state.video.elapsedSec);
    if (state.video.lastSpokenBeatId !== beat.id) {
      state.video.lastSpokenBeatId = beat.id;
      speakBeatLines(beat);
    }
    startVideoTicker();
    render();
  }

  function pauseVideo() {
    state.video.isPlaying = false;
    state.video.lastSpokenBeatId = -1;
    clearInterval(rafTimer);
    stopAudio();
    if (state.step === 'video') render();
  }

  function replayVideo() {
    stopAudio();
    clearInterval(rafTimer);
    closeModal();
    state.video.hasStarted = true;
    state.video.elapsedSec = 0;
    state.video.lastSpokenBeatId = -1;
    state.video.isPlaying = true;
    const beat = getActiveBeat(0);
    state.video.lastSpokenBeatId = beat.id;
    speakBeatLines(beat);
    startVideoTicker();
    render();
  }

  function exitFullScreen() {
    state.video.isFullScreen = false;
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    }
  }

  function toggleFullScreen() {
    const wrapper = document.getElementById('player-wrapper');
    if (!state.video.isFullScreen) {
      state.video.isFullScreen = true;
      if (wrapper && wrapper.requestFullscreen) {
        wrapper.requestFullscreen().catch(() => {});
      }
    } else {
      exitFullScreen();
    }
    render();
  }

  document.addEventListener('fullscreenchange', () => {
    if (!document.fullscreenElement && state.video.isFullScreen) {
      state.video.isFullScreen = false;
      if (state.step === 'video') render();
    }
  });

  function updateLiveVideoDom() {
    if (state.step !== 'video') return;
    const sec = state.video.elapsedSec;
    const beat = getActiveBeat(sec);
    const beatDur = Math.max(1, beat.endSec - beat.startSec);
    const prog = Math.max(0, Math.min(1, (sec - beat.startSec) / beatDur));

    const timeEl = document.getElementById('video-time-label');
    if (timeEl) timeEl.textContent = `00:${String(Math.round(sec)).padStart(2, '0')} / 00:30`;

    const fillEl = document.getElementById('video-progress-fill');
    if (fillEl) fillEl.style.width = `${(sec / 30) * 100}%`;

    const beatTitleEl = document.getElementById('video-beat-title');
    if (beatTitleEl) beatTitleEl.textContent = state.video.hasStarted ? beat.title : 'Ready to Play';

    const stageImg = document.getElementById('stage-media-img');
    if (stageImg) {
      if (!stageImg.dataset.beatId || Number(stageImg.dataset.beatId) !== beat.id) {
        stageImg.src = beat.image;
        stageImg.dataset.beatId = String(beat.id);
      }
      const scale = 1 + 0.08 * prog;
      const shake = beat.id === 2 && prog > 0.05 && prog < 0.22 ? Math.sin(sec * 45) * 10 : 0;
      stageImg.style.transform = `scale(${scale.toFixed(3)}) translateX(${(shake - 16 * prog).toFixed(1)}px)`;
    }

    const flyingEl = document.getElementById('flying-poop-el');
    if (flyingEl) {
      if (beat.id === 3 || beat.id === 4) {
        flyingEl.style.display = 'block';
        flyingEl.style.left = `${15 + 65 * prog}%`;
        flyingEl.style.top = `${45 - 25 * Math.sin(prog * Math.PI)}%`;
        flyingEl.style.transform = `rotate(${prog * 540}deg)`;
      } else {
        flyingEl.style.display = 'none';
      }
    }

    const voiceEl = document.getElementById('overlay-voice-pill');
    if (voiceEl) {
      const emo = state.video.subtitleEmotion ? ` · ${state.video.subtitleSpeaker} (${state.video.subtitleEmotion})` : '';
      voiceEl.textContent = `${beat.timeRange}${emo}`;
    }

    const subBox = document.getElementById('subtitle-box');
    if (subBox) {
      const text = state.video.subtitleBeatId === beat.id && state.video.subtitleText
        ? state.video.subtitleText
        : beat.lines[0].english.replace(/\*\*/g, '');
      const words = text.split(/\s+/).filter(Boolean);
      const reached = state.video.subtitleBeatId === beat.id ? state.video.spokenWords : words.length;
      subBox.innerHTML = words
        .map((w, i) => (i < reached ? `<span>${escapeHtml(w)}</span>` : `<span class="subtitle-word-future">${escapeHtml(w)}</span>`))
        .join(' ');
    }
  }

  // --- Regenerate One Beat (Dynamic Story Variation) ---
  function regenerateBeat(beatId) {
    const idx = state.beats.findIndex((b) => b.id === beatId);
    if (idx === -1) return;
    const picked = state.expressions.slice(0, 9);
    const e1 = picked[0]?.phrase || 'was going to';
    const e2 = picked[2]?.phrase || "I'm really bummed";
    const e3 = picked[3]?.phrase || "I'm over the moon";
    const e4 = picked[4]?.phrase || 'low point';
    const e5 = picked[5]?.phrase || 'make fun of';
    const e6 = picked[6]?.phrase || 'crossed the line';
    const e7 = picked[7]?.phrase || 'jerk';
    const e8 = picked[8]?.phrase || 'belong in jail';

    const variants = {
      1: {
        scene: `${state.scene.place} Friend A and Friend B stroll cheerfully along the sunny waves while a cute smiling cartoon poop sits right ahead on the sand.`,
        lines: [
          { speaker: 'Friend A', english: `What a sunny beach! I **${e1}** nap today.`, korean: 'A: 해변 날씨 최고다! 오늘 집에서 낮잠 자려고 했는데.' },
          { speaker: 'Friend B', english: '**If we had** boards, we **could** surf too.', korean: 'B: 보드가 있다면 서핑도 할 수 있을 텐데!' },
        ],
      },
      2: {
        scene: 'Squish! Friend A steps right onto the cute smiling poop on the sand and groans while Friend B bursts out laughing.',
        lines: [
          { speaker: 'Friend A', english: `Oh no, poop on my shoe! **${e2}**!`, korean: 'A: 아, 신발에 똥 밟았어! 기분 진짜 안 좋아!' },
          { speaker: 'Friend B', english: `Hahaha! Sorry, **${e3}** right now!`, korean: 'B: 하하하! 미안한데 나 지금 너무 웃겨!' },
        ],
      },
      3: {
        scene: 'Friend A picks up the cute poop and playfully flings it at Friend B, who gasps in shock and throws it right back!',
        lines: [
          { speaker: 'Friend A', english: `Today hit a **${e4}**! Don't **${e5}** me!`, korean: 'A: 오늘 진짜 최악의 순간이야! 날 놀리지 마!' },
          { speaker: 'Friend B', english: `Whoa! Now you really **${e6}**! Catch!`, korean: 'B: 헉! 너 지금 완전 선 넘었어! 받아라!' },
        ],
      },
      4: {
        scene: 'A stray cute poop splats onto the Hawaiian-shirt passerby! He yells comically and launches a cheerful beach-wide poop fight.',
        lines: [
          { speaker: 'Passerby', english: `Hey! Watch it, you **${e7}**s! You **${e8}**!`, korean: 'Passerby: 야! 조심해, 이 녀석들아! 너희 감옥에 가야 해!' },
        ],
      },
    };
    const v = variants[beatId];
    if (v) {
      state.beats[idx] = { ...state.beats[idx], scene: v.scene, lines: v.lines };
      render();
    }
  }

  // --- Browser Video Export (Canvas + MediaRecorder) ---
  async function downloadWebVideo() {
    if (state.exportStatus && state.exportStatus.type === 'loading') return;
    const canvas = document.createElement('canvas');
    canvas.width = 1280;
    canvas.height = 720;
    const ctx = canvas.getContext('2d');
    if (!ctx || typeof MediaRecorder === 'undefined') {
      state.exportStatus = { type: 'error', text: 'Your browser does not support in-browser video recording.' };
      render();
      return;
    }

    state.exportStatus = { type: 'loading', progress: 0 };
    render();

    try {
      const loadedImgs = {};
      await Promise.all(
        state.beats.map(
          (b) =>
            new Promise((resolve) => {
              const img = new Image();
              img.onload = () => { loadedImgs[b.id] = img; resolve(); };
              img.onerror = () => resolve();
              img.src = b.image;
            })
        )
      );

      const stream = canvas.captureStream(15);
      const mime = MediaRecorder.isTypeSupported('video/webm;codecs=vp9')
        ? 'video/webm;codecs=vp9'
        : 'video/webm';
      const recorder = new MediaRecorder(stream, { mimeType: mime });
      const chunks = [];
      recorder.ondataavailable = (e) => { if (e.data.size > 0) chunks.push(e.data); };

      const donePromise = new Promise((resolve) => {
        recorder.onstop = () => {
          const blob = new Blob(chunks, { type: 'video/webm' });
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'PoopAttack_30s_Movie.webm';
          document.body.appendChild(a);
          a.click();
          a.remove();
          setTimeout(() => URL.revokeObjectURL(url), 5000);
          resolve();
        };
      });

      recorder.start();
      const fps = 15;
      const totalSeconds = 12; // Fast 12s export covering all 4 beats cleanly
      const totalFrames = fps * totalSeconds;
      for (let f = 0; f < totalFrames; f++) {
        const mappedSec = (f / totalFrames) * 30;
        const beat = getActiveBeat(mappedSec);
        const beatDur = Math.max(1, beat.endSec - beat.startSec);
        const prog = Math.max(0, Math.min(1, (mappedSec - beat.startSec) / beatDur));

        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, 1280, 720);
        const img = loadedImgs[beat.id];
        if (img) {
          const scale = 1 + 0.08 * prog;
          const dw = 1280 * scale;
          const dh = 720 * scale;
          ctx.drawImage(img, (1280 - dw) / 2, (720 - dh) / 2, dw, dh);
        }
        ctx.fillStyle = 'rgba(33, 122, 116, 0.85)';
        ctx.fillRect(28, 24, 300, 44);
        ctx.fillStyle = '#fff';
        ctx.font = 'bold 24px Nunito, sans-serif';
        ctx.fillText(`${beat.title} (${beat.timeRange})`, 44, 54);

        if (state.subtitlesEnabled && beat.lines.length) {
          const lIdx = beat.lines.length > 1 && prog >= 0.5 ? 1 : 0;
          const sub = beat.lines[lIdx].english.replace(/\*\*/g, '');
          ctx.fillStyle = 'rgba(0, 0, 0, 0.65)';
          ctx.fillRect(64, 600, 1152, 86);
          ctx.fillStyle = '#fff';
          ctx.font = 'bold 34px Nunito, sans-serif';
          ctx.textAlign = 'center';
          ctx.fillText(sub, 640, 654, 1100);
          ctx.textAlign = 'left';
        }
        state.exportStatus = { type: 'loading', progress: Math.round(((f + 1) / totalFrames) * 100) };
        if (f % 10 === 0) {
          const btn = document.getElementById('download-video-btn');
          if (btn) btn.textContent = `Rendering Video... ${state.exportStatus.progress}%`;
        }
        await new Promise((r) => setTimeout(r, 40));
      }
      recorder.stop();
      await donePromise;
      state.exportStatus = { type: 'success', text: 'Downloaded PoopAttack_30s_Movie.webm!' };
    } catch (err) {
      state.exportStatus = { type: 'error', text: err.message || 'Download failed.' };
    }
    render();
  }

  // --- Modals ---
  function openModal(html) {
    const root = document.getElementById('modal-root');
    root.innerHTML = `<div class="modal-card">${html}</div>`;
    root.classList.remove('hidden');
  }

  function closeModal() {
    const root = document.getElementById('modal-root');
    root.classList.add('hidden');
    root.innerHTML = '';
  }

  function openExpressionModal(existing = null) {
    openModal(`
      <h2 class="section-title">${existing ? 'Edit Expression' : 'Add Movie Expression'}</h2>
      <div>
        <label class="field-label">English Expression</label>
        <input id="m-phrase" class="text-input" value="${escapeHtml(existing?.phrase || '')}" placeholder="e.g. over the moon" />
      </div>
      <div>
        <label class="field-label">Korean Meaning</label>
        <input id="m-korean" class="text-input" value="${escapeHtml(existing?.korean || '')}" placeholder="e.g. 뛸 듯이 기뻐" />
      </div>
      <div>
        <label class="field-label">Movie Title</label>
        <input id="m-movie" class="text-input" value="${escapeHtml(existing?.movie || '')}" placeholder="e.g. Inside Out 2" />
      </div>
      <div class="row-actions">
        <button id="m-cancel" class="btn btn-outline" style="flex:1">Cancel</button>
        <button id="m-save" class="btn btn-primary" style="flex:1">Save</button>
      </div>
    `);
    document.getElementById('m-cancel').onclick = closeModal;
    document.getElementById('m-save').onclick = () => {
      const phrase = document.getElementById('m-phrase').value.trim();
      const korean = document.getElementById('m-korean').value.trim() || '뜻 추가됨';
      const movie = document.getElementById('m-movie').value.trim() || 'My Movie';
      if (!phrase) return;
      if (existing) {
        existing.phrase = phrase;
        existing.korean = korean;
        existing.movie = movie;
      } else {
        state.expressions.push({ id: `e_${Date.now()}`, phrase, korean, movie });
      }
      closeModal();
      render();
    };
  }

  function openBeatEditor(beat) {
    const linesHtml = beat.lines
      .map(
        (l, idx) => `
      <div style="border-top:1px solid var(--border); padding-top:10px;">
        <div class="speaker-tag">Line ${idx + 1} (${escapeHtml(l.speaker)})</div>
        <input id="b-eng-${idx}" class="text-input" style="margin-top:6px" value="${escapeHtml(l.english)}" />
        <input id="b-kor-${idx}" class="text-input" style="margin-top:6px" value="${escapeHtml(l.korean)}" />
      </div>`
      )
      .join('');

    openModal(`
      <h2 class="section-title">Edit ${escapeHtml(beat.title)} (${beat.timeRange})</h2>
      <div>
        <label class="field-label">Scene Description</label>
        <textarea id="b-scene" class="text-area" rows="3">${escapeHtml(beat.scene)}</textarea>
      </div>
      <p class="section-sub" style="font-size:14px; color:var(--primary)">Tip: Wrap target expressions in **double asterisks** to bold them.</p>
      ${linesHtml}
      <div class="row-actions">
        <button id="b-cancel" class="btn btn-outline" style="flex:1">Cancel</button>
        <button id="b-save" class="btn btn-primary" style="flex:1">Save Beat</button>
      </div>
    `);
    document.getElementById('b-cancel').onclick = closeModal;
    document.getElementById('b-save').onclick = () => {
      beat.scene = document.getElementById('b-scene').value.trim();
      beat.lines = beat.lines.map((l, idx) => ({
        ...l,
        english: document.getElementById(`b-eng-${idx}`).value.trim(),
        korean: document.getElementById(`b-kor-${idx}`).value.trim(),
      }));
      closeModal();
      render();
    };
  }

  function renderQuizMarkup() {
    const correctCount = QUIZ_QUESTIONS.filter((q) => state.quizSelections[q.id] === q.correctIndex).length;
    const qsHtml = QUIZ_QUESTIONS.map((q, idx) => {
      const sel = state.quizSelections[q.id];
      const isAnswered = sel !== undefined;
      const isCorrect = sel === q.correctIndex;
      const opts = q.options
        .map((opt, oIdx) => {
          const cls = sel === oIdx ? (oIdx === q.correctIndex ? 'correct' : 'wrong') : '';
          return `<button class="quiz-option ${cls}" data-qid="${q.id}" data-oid="${oIdx}">
            <span>${escapeHtml(opt)}</span>
            <span>${sel === oIdx ? (oIdx === q.correctIndex ? '✓' : '✕') : ''}</span>
          </button>`;
        })
        .join('');
      const feedback = isAnswered
        ? `<div class="${isCorrect ? 'success-banner' : 'warning-banner'}" style="margin-top:10px">
            ${isCorrect ? `Correct! ${escapeHtml(q.explanation)}` : 'Try Again! Listen closely to the character and pick another option.'}
          </div>`
        : '';
      return `
        <div class="card">
          <div class="movie-badge">Question ${idx + 1}</div>
          <h3 class="expr-phrase" style="margin-bottom:12px">${escapeHtml(q.question)}</h3>
          <div style="display:flex; flex-direction:column; gap:10px">${opts}</div>
          ${feedback}
        </div>
      `;
    }).join('');

    return {
      header: `
        <div class="card hero-card">
          <img src="./assets/images/img_poop_icon.jpg" class="hero-mascot" alt="Quiz mascot" />
          <div>
            <h2 class="section-title">Quick Comprehension Quiz</h2>
            <p class="section-sub">Score: ${correctCount} / ${QUIZ_QUESTIONS.length} correct</p>
          </div>
        </div>
      `,
      qsHtml,
    };
  }

  function openQuizPopup() {
    const { header, qsHtml } = renderQuizMarkup();
    openModal(`
      ${header}
      ${qsHtml}
      <button id="popup-finish" class="btn btn-primary">Finish & See Wrap-Up</button>
      <div class="row-actions">
        <button id="popup-replay" class="btn btn-outline btn-sm" style="flex:1">Replay Video</button>
        <button id="popup-close" class="btn btn-outline btn-sm" style="flex:1">Close</button>
      </div>
    `);
    document.querySelectorAll('#modal-root .quiz-option').forEach((btn) => {
      btn.onclick = () => {
        state.quizSelections[Number(btn.dataset.qid)] = Number(btn.dataset.oid);
        openQuizPopup();
      };
    });
    document.getElementById('popup-finish').onclick = () => {
      closeModal();
      goTo('final');
    };
    document.getElementById('popup-replay').onclick = replayVideo;
    document.getElementById('popup-close').onclick = closeModal;
  }

  function goTo(stepId) {
    if (state.step === 'video' && stepId !== 'video') {
      pauseVideo();
      exitFullScreen();
    }
    state.step = stepId;
    render();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // --- Render Screens ---
  function render() {
    const nav = document.getElementById('step-nav');
    nav.innerHTML = STEPS.map(
      (s) =>
        `<button class="step-pill ${s.id === state.step ? 'active' : ''}" data-step="${s.id}">${s.num}. ${s.label}</button>`
    ).join('');
    nav.querySelectorAll('.step-pill').forEach((btn) => {
      btn.onclick = () => goTo(btn.dataset.step);
    });

    const root = document.getElementById('screen-root');

    if (state.step === 'input') {
      const uniqueMovies = new Set(state.expressions.map((e) => e.movie.trim()).filter(Boolean)).size;
      const warn = getValidationWarning();
      root.innerHTML = `
        <div class="card hero-card">
          <img src="./assets/images/img_poop_icon.jpg" alt="Cute 3D poop" class="hero-mascot" />
          <div>
            <h2 class="section-title">Step 1 · Movie Expressions</h2>
            <p class="section-sub">${state.expressions.length} expressions from ${uniqueMovies} movies ready for your 30-second animated clip.</p>
          </div>
        </div>
        ${warn ? `<div class="warning-banner">⚠️ ${escapeHtml(warn)}</div>` : ''}
        <div class="row-actions">
          <button id="add-expr-btn" class="btn btn-outline" style="flex:1">＋ Add Expression</button>
          <button id="reset-expr-btn" class="btn btn-outline btn-sm">↺ Reset 9 Defaults</button>
        </div>
        <div class="expr-grid">
          ${state.expressions
            .map(
              (e) => `
            <div class="card expr-card">
              <div>
                <div class="movie-badge">🎬 ${escapeHtml(e.movie)}</div>
                <p class="expr-phrase">"${escapeHtml(e.phrase)}"</p>
                <p class="expr-korean">${escapeHtml(e.korean)}</p>
              </div>
              <div style="display:flex; gap:6px">
                <button class="icon-btn edit-expr" data-id="${e.id}" title="Edit">✏️</button>
                <button class="icon-btn del-expr" data-id="${e.id}" title="Delete">🗑️</button>
              </div>
            </div>
          `
            )
            .join('')}
        </div>
        <button id="to-scene-btn" class="btn btn-primary">Next: Set the Scene →</button>
      `;
      document.getElementById('add-expr-btn').onclick = () => openExpressionModal(null);
      document.getElementById('reset-expr-btn').onclick = () => {
        state.expressions = structuredClone(DEFAULT_EXPRESSIONS);
        render();
      };
      root.querySelectorAll('.edit-expr').forEach((b) => {
        b.onclick = () => openExpressionModal(state.expressions.find((x) => x.id === b.dataset.id));
      });
      root.querySelectorAll('.del-expr').forEach((b) => {
        b.onclick = () => {
          state.expressions = state.expressions.filter((x) => x.id !== b.dataset.id);
          render();
        };
      });
      document.getElementById('to-scene-btn').onclick = () => goTo('scene');
      return;
    }

    if (state.step === 'scene') {
      root.innerHTML = `
        <div>
          <h2 class="section-title">Step 2 · Scene & Style</h2>
          <p class="section-sub">Customize the sunny beach setting, characters, situation, and animation style.</p>
        </div>
        <div class="card">
          <label class="field-label">🏖️ Place</label>
          <textarea id="scene-place" class="text-area" rows="2">${escapeHtml(state.scene.place)}</textarea>
        </div>
        <div class="card">
          <label class="field-label">🧑 Characters</label>
          <textarea id="scene-chars" class="text-area" rows="4">${escapeHtml(state.scene.characters)}</textarea>
        </div>
        <div class="card">
          <label class="field-label">🎬 Situation</label>
          <textarea id="scene-sit" class="text-area" rows="3">${escapeHtml(state.scene.situation)}</textarea>
        </div>
        <div class="card">
          <label class="field-label">✨ Style Menu</label>
          <p class="section-sub">Pre-selected: cute, vibrant, family-friendly 3D Pixar/Disney style</p>
          <div class="style-chips">
            ${state.scene.styleOptions
              .map(
                (s) =>
                  `<button class="style-chip ${s === state.scene.style ? 'selected' : ''}" data-style="${escapeHtml(s)}">${s === state.scene.style ? '✓ ' : ''}${escapeHtml(s)}</button>`
              )
              .join('')}
          </div>
        </div>
        <button id="to-story-btn" class="btn btn-primary">Next: Build Storyboard →</button>
      `;
      document.getElementById('scene-place').oninput = (e) => (state.scene.place = e.target.value);
      document.getElementById('scene-chars').oninput = (e) => (state.scene.characters = e.target.value);
      document.getElementById('scene-sit').oninput = (e) => (state.scene.situation = e.target.value);
      root.querySelectorAll('.style-chip').forEach((btn) => {
        btn.onclick = () => {
          state.scene.style = btn.dataset.style;
          render();
        };
      });
      document.getElementById('to-story-btn').onclick = () => goTo('storyboard');
      return;
    }

    if (state.step === 'storyboard') {
      const totalWords = countTotalWords(state.beats);
      const inRange = totalWords >= 60 && totalWords <= 70;
      root.innerHTML = `
        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:10px">
          <div>
            <h2 class="section-title">Step 3 · 4-Beat Storyboard</h2>
            <div class="word-pill ${inRange ? 'ok' : 'warn'}">Total: ${totalWords} words (Target: 60–70 words · 30s)</div>
          </div>
          <button id="reset-story-btn" class="btn btn-outline btn-sm">↺ Reset Script</button>
        </div>
        <div class="beat-grid">
          ${state.beats
            .map(
              (b) => `
            <div class="card">
              <div style="display:flex; justify-content:space-between; align-items:center">
                <h3 class="expr-phrase">${escapeHtml(b.title)}</h3>
                <span class="movie-badge" style="background:var(--primary-soft); color:var(--primary-ink); margin:0">${b.timeRange}</span>
              </div>
              <img src="${b.image}" alt="${escapeHtml(b.title)}" class="beat-preview-img" />
              <p class="kor-line" style="color:var(--ink)"><strong>Scene:</strong> ${escapeHtml(b.scene)}</p>
              <div class="dialogue-box">
                ${b.lines
                  .map(
                    (l) => `
                  <div>
                    <div class="speaker-tag">${escapeHtml(l.speaker)}</div>
                    <p class="eng-line">${formatBoldExpressions(l.english)}</p>
                    <p class="kor-line">${escapeHtml(l.korean)}</p>
                  </div>
                `
                  )
                  .join('')}
              </div>
              <div class="row-actions">
                <button class="btn btn-outline btn-sm edit-beat-btn" data-id="${b.id}" style="flex:1">✏️ Edit Beat</button>
                <button class="btn btn-soft btn-sm regen-beat-btn" data-id="${b.id}" style="flex:1">✨ Regenerate</button>
              </div>
            </div>
          `
            )
            .join('')}
        </div>
        <button id="to-video-btn" class="btn btn-secondary">▶ Watch 30s Animated Film</button>
      `;
      document.getElementById('reset-story-btn').onclick = () => {
        state.beats = structuredClone(DEFAULT_BEATS);
        render();
      };
      root.querySelectorAll('.edit-beat-btn').forEach((btn) => {
        btn.onclick = () => openBeatEditor(state.beats.find((b) => b.id === Number(btn.dataset.id)));
      });
      root.querySelectorAll('.regen-beat-btn').forEach((btn) => {
        btn.onclick = () => regenerateBeat(Number(btn.dataset.id));
      });
      document.getElementById('to-video-btn').onclick = () => goTo('video');
      return;
    }

    if (state.step === 'video') {
      const beat = getActiveBeat(state.video.elapsedSec);
      const fsClass = state.video.isFullScreen ? 'css-fullscreen' : '';
      root.innerHTML = `
        <h2 class="section-title">Step 4 · 30s Animated Short Film</h2>
        <div id="player-wrapper" class="player-wrapper ${fsClass}">
          ${
            !state.video.hasStarted
              ? `
            <div class="cover-stage">
              <img src="./assets/images/img_poop_icon.jpg" alt="Cute smiling 3D poop character" class="cover-poop" />
              <div>
                <div class="movie-badge" style="background:var(--primary); color:#fff">30s 3D Animated Clip</div>
                <h3 style="margin:6px 0 12px; font-size:28px; font-weight:900">Poop Attack!</h3>
                <button id="cover-play-btn" class="btn btn-secondary btn-sm" style="width:auto; padding:12px 22px">▶ Play Movie</button>
              </div>
            </div>
          `
              : `
            <img id="stage-media-img" class="stage-media" src="${beat.image}" data-beat-id="${beat.id}" alt="${escapeHtml(beat.scene)}" />
            <div class="stage-scrim"></div>
            <img id="flying-poop-el" class="flying-poop" src="./assets/images/img_poop_icon.jpg" style="display:none" alt="" />
            ${
              state.subtitlesEnabled
                ? `<div class="subtitle-overlay"><div id="subtitle-box" class="subtitle-box"></div></div>`
                : ''
            }
          `
          }
          <div class="player-top-overlay">
            <div>
              ${
                state.video.hasStarted
                  ? `<span id="overlay-voice-pill" class="overlay-pill" style="cursor:default">${beat.timeRange}</span>`
                  : ''
              }
            </div>
            <div style="display:flex; gap:8px">
              ${
                state.video.hasStarted
                  ? `<button id="overlay-cc-btn" class="overlay-pill ${state.subtitlesEnabled ? 'cc-on' : ''}">${state.subtitlesEnabled ? 'CC On' : 'CC Off'}</button>`
                  : ''
              }
              <button id="overlay-fs-btn" class="overlay-pill">${state.video.isFullScreen ? '✕ 전체화면 닫기' : '⛶ 전체화면'}</button>
            </div>
          </div>
        </div>

        <div class="card" style="display:flex; flex-direction:column; gap:12px">
          <div style="display:flex; justify-content:space-between; align-items:center">
            <strong id="video-beat-title" style="font-size:18px">${state.video.hasStarted ? escapeHtml(beat.title) : 'Ready to Play'}</strong>
            <strong id="video-time-label" style="color:var(--primary)">00:${String(Math.round(state.video.elapsedSec)).padStart(2, '0')} / 00:30</strong>
          </div>
          <div class="progress-track"><div id="video-progress-fill" class="progress-fill" style="width:${(state.video.elapsedSec / 30) * 100}%"></div></div>
          <div class="row-actions">
            <button id="ctrl-play-btn" class="btn btn-primary" style="flex:2; width:auto">
              ${state.video.isPlaying ? '⏸ Pause' : state.video.hasStarted && state.video.elapsedSec < 30 ? '▶ Resume' : '▶ Play'}
            </button>
            <button id="ctrl-replay-btn" class="btn btn-outline" style="flex:1">↺ Replay</button>
            <button id="ctrl-cc-btn" class="btn btn-outline">${state.subtitlesEnabled ? 'CC On' : 'CC Off'}</button>
            <button id="ctrl-fs-btn" class="btn btn-outline">${state.video.isFullScreen ? 'Exit Full' : '⛶ Full Screen'}</button>
          </div>
        </div>

        <div class="beat-jump-row">
          ${state.beats
            .map(
              (b) => `
            <button class="beat-jump-btn ${state.video.hasStarted && beat.id === b.id ? 'active' : ''}" data-start="${b.startSec}" data-bid="${b.id}">
              <div style="font-weight:800">Beat ${b.id}</div>
              <div style="font-size:13px; color:var(--muted)">${b.timeRange}</div>
            </button>
          `
            )
            .join('')}
        </div>

        <button id="to-quiz-btn" class="btn btn-secondary">📝 Start Quiz →</button>
      `;

      const coverBtn = document.getElementById('cover-play-btn');
      if (coverBtn) coverBtn.onclick = playOrResumeVideo;
      const ccTop = document.getElementById('overlay-cc-btn');
      if (ccTop) {
        ccTop.onclick = () => {
          state.subtitlesEnabled = !state.subtitlesEnabled;
          render();
        };
      }
      document.getElementById('overlay-fs-btn').onclick = toggleFullScreen;
      document.getElementById('ctrl-fs-btn').onclick = toggleFullScreen;
      document.getElementById('ctrl-cc-btn').onclick = () => {
        state.subtitlesEnabled = !state.subtitlesEnabled;
        render();
      };
      document.getElementById('ctrl-play-btn').onclick = () => {
        if (state.video.isPlaying) pauseVideo();
        else playOrResumeVideo();
      };
      document.getElementById('ctrl-replay-btn').onclick = replayVideo;
      root.querySelectorAll('.beat-jump-btn').forEach((btn) => {
        btn.onclick = () => {
          stopAudio();
          state.video.hasStarted = true;
          state.video.elapsedSec = Number(btn.dataset.start);
          state.video.lastSpokenBeatId = -1;
          state.video.isPlaying = true;
          const b = getActiveBeat(state.video.elapsedSec);
          state.video.lastSpokenBeatId = b.id;
          speakBeatLines(b);
          startVideoTicker();
          render();
        };
      });
      document.getElementById('to-quiz-btn').onclick = () => goTo('quiz');
      updateLiveVideoDom();
      return;
    }

    if (state.step === 'quiz') {
      const { header, qsHtml } = renderQuizMarkup();
      root.innerHTML = `
        ${header}
        ${qsHtml}
        <button id="quiz-to-final-btn" class="btn btn-primary">See Wrap-Up →</button>
      `;
      root.querySelectorAll('.quiz-option').forEach((btn) => {
        btn.onclick = () => {
          state.quizSelections[Number(btn.dataset.qid)] = Number(btn.dataset.oid);
          render();
        };
      });
      document.getElementById('quiz-to-final-btn').onclick = () => goTo('final');
      return;
    }

    if (state.step === 'final') {
      const correctCount = QUIZ_QUESTIONS.filter((q) => state.quizSelections[q.id] === q.correctIndex).length;
      const exportBanner = state.exportStatus
        ? state.exportStatus.type === 'success'
          ? `<div class="success-banner">✓ ${escapeHtml(state.exportStatus.text)}</div>`
          : state.exportStatus.type === 'error'
          ? `<div class="warning-banner">⚠️ ${escapeHtml(state.exportStatus.text)}</div>`
          : ''
        : '';

      root.innerHTML = `
        <div class="card hero-card" style="flex-direction:column; text-align:center; padding:28px">
          <img src="./assets/images/img_poop_icon.jpg" alt="Poop Attack trophy" class="hero-mascot" style="width:110px; height:110px" />
          <div>
            <h2 class="section-title">Awesome Job!</h2>
            <p class="section-sub">You practiced ${state.expressions.length} movie expressions and scored <strong>${correctCount} / ${QUIZ_QUESTIONS.length}</strong> on the quiz!</p>
          </div>
        </div>
        <div style="display:flex; flex-direction:column; gap:12px">
          <button id="final-replay-btn" class="btn btn-primary">↺ Replay Video</button>
          <button id="final-quiz-btn" class="btn btn-secondary">📝 Retake Quiz</button>
          <button id="download-video-btn" class="btn btn-outline" ${state.exportStatus?.type === 'loading' ? 'disabled' : ''}>
            ${state.exportStatus?.type === 'loading' ? `Rendering Video... ${state.exportStatus.progress}%` : '⬇ Download Video'}
          </button>
        </div>
        ${exportBanner}
        <div class="card">
          <h3 class="expr-phrase" style="margin-bottom:12px">Expressions You Practiced</h3>
          <div style="display:flex; flex-wrap:wrap; gap:8px">
            ${state.expressions
              .map(
                (e) => `
              <div style="background:var(--surface-soft); padding:8px 14px; border-radius:14px">
                <div style="font-weight:800; color:var(--primary)">${escapeHtml(e.phrase)}</div>
                <div style="font-size:13px; color:var(--muted)">${escapeHtml(e.korean)}</div>
              </div>
            `
              )
              .join('')}
          </div>
        </div>
        <div class="credit-footer">Made with Google AI Studio · Script by Lee Ju Won · Assisted by NotebookLM</div>
      `;
      document.getElementById('final-replay-btn').onclick = () => {
        state.step = 'video';
        replayVideo();
      };
      document.getElementById('final-quiz-btn').onclick = () => {
        state.quizSelections = {};
        goTo('quiz');
      };
      document.getElementById('download-video-btn').onclick = downloadWebVideo;
    }
  }

  if ('speechSynthesis' in window && window.speechSynthesis.onvoiceschanged !== undefined) {
    window.speechSynthesis.onvoiceschanged = () => pickMaleVoices();
  }

  render();
})();
