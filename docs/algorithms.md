# AERIS — Algorithms Documentation

## NSI — Nervous System Index

Reference: Front. Physiol. 2021 (https://doi.org/10.3389/fphys.2021.625789)

Formula:
```
circadianFactor = +5 (morning 6-10), -5 (evening 18-22), 0 otherwise
stressScore = (hr/200)*40 + ((100-hrv)/100)*30 + ((1-sleep)*20) + circadianFactor
```

Classification:
- stressScore > 65 -> HYPERAROUSAL (high stress)
- stressScore < 35 -> HYPOAROUSAL (low energy)
- else -> BALANCED

## BCI — Breath Capacity Index

Reference: Respir. Physiol. Neurobiol. 2018 (https://doi.org/10.1016/j.resp.2018.05.007)

Formula:
```
d1=0.4, d2=0.3, d3=0.15, d4=0.15 (sum = 1.0)
relativeHold = min(hold/maxRef, 2) / 2
BCI = (d1*relativeHold + d2*co2 + d3*stability + d4*progress) * 100
```

## AI Recommendation Algorithm

1. Safety filter (CheckSafety) — excludes blocked protocols
2. NSI filter — HYPERAROUSAL excludes SYMPATHETIC, HYPOAROUSAL excludes PARASYMPATHETIC
3. Sort by relevance:
   - +0.2 if category matches time of day
   - +0.1 if BCI < 40 and BEGINNER
   - +0.05 if protocol not yet practiced
4. Return top 5

## Level Progression

| Sessions | BCI | Level |
|----------|-----|-------|
| >= 100 | > 75 | 5 (Guide) |
| >= 50 | > 60 | 4 (Master) |
| >= 25 | > 40 | 3 (Adept) |
| >= 10 | any | 2 (Practicer) |
| < 10 | any | 1 (Novice) |

## Phase 2 Protocols (Research-backed)

### Physiological Sigh (Stanford/Huberman Lab)
Double inhale through nose + long exhale. Rapidly reduces stress by reinflating collapsed alveoli and increasing O2/CO2 exchange.

### Resonance Breathing (0.1 Hz)
6 breaths/minute (inhale:exhale 4:6 or 5:5). Maximizes HRV coherence and baroreflex sensitivity.

### Wim Hof Method
30-40 deep breaths + breath hold. Increases epinephrine, reduces pro-inflammatory cytokines (TNF-alpha, IL-6, IL-8), increases anti-inflammatory IL-10.

### Coherent Breathing (5 breaths/min)
5-6 breaths/min. Clinically proven to reduce anxiety and depression scores.
