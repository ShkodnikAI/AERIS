# AERIS Algorithms Documentation

## Overview

AERIS uses two primary indices to assess user state and provide personalized recommendations:

1. **NSI (Nervous System Index)** - Assesses autonomic nervous system balance
2. **BCI (Breath Capacity Index)** - Measures respiratory fitness and progress

---

## NSI - Nervous System Index

### Scientific Background

Based on research from:
- **Front. Physiol. 2021** - [DOI: 10.3389/fphys.2021.625789](https://doi.org/10.3389/fphys.2021.625789)
- Heart rate variability as a marker of autonomic function

### Formula

```
arousalScore = (HR × -0.3) + (HRV × 0.5) + (sleepQuality × 20 × circadianFactor) + 50
```

Where:
- **HR** = Normalized heart rate (0-1 scale from 50-100 BPM range)
- **HRV** = Normalized HRV SDNN (0-1 scale from 20-100ms range)
- **sleepQuality** = User-reported or sensor-derived (0-1)
- **circadianFactor** = Time-of-day adjustment (see below)

### Circadian Factors

| Time of Day | Factor | Rationale |
|-------------|--------|-----------|
| 6:00-10:00  | 1.1    | Morning cortisol peak |
| 11:00-14:00 | 1.0    | Baseline |
| 15:00-17:00 | 0.95   | Afternoon dip |
| 18:00-22:00 | 0.9    | Evening relaxation |
| 23:00-05:00 | 0.8-0.85| Night rest period |

### State Classification

| Score Range | State | Recommendation |
|-------------|-------|----------------|
| > 70 | HYPERAROUSAL | Relaxation protocols (no sympathetic stimulation) |
| 30-70 | BALANCED | All protocols available |
| < 30 | HYPOAROUSAL | Energy protocols (avoid sedating) |

---

## BCI - Breath Capacity Index

### Scientific Background

Based on research from:
- **Respir. Physiol. Neurobiol. 2018** - [DOI: 10.1016/j.resp.2018.05.007](https://doi.org/10.1016/j.resp.2018.05.007)
- Clinical breath hold assessments and CO₂ tolerance testing

### Formula

```
BCI = (d1 × relativeHold) + (d2 × co2Tolerance) + (d3 × stabilityScore) + (d4 × progressScore)
```

**Weights (sum = 1.0):**
- d1 = 0.4 (Breath hold duration)
- d2 = 0.3 (CO₂ tolerance)
- d3 = 0.15 (Rhythm stability)
- d4 = 0.15 (Weekly progress)

### Components

#### 1. Relative Hold Duration
```
relativeHold = min(holdDuration / referenceForAge, 2.0) / 2.0
```

Age-based reference values:
| Age Range | Reference (seconds) |
|-----------|---------------------|
| 18-29 | 40 |
| 30-39 | 38 |
| 40-49 | 35 |
| 50-59 | 32 |
| 60-69 | 28 |
| 70+ | 25 |

#### 2. CO₂ Tolerance
Derived from Control Pause test (time to first urge to breathe after normal exhale):
```
<10s: poor (0-0.25)
10-20s: fair (0.25-0.5)
20-40s: good (0.5-0.9)
>40s: excellent (0.9-1.0)
```

#### 3. Rhythm Stability
Calculated from coefficient of variation of breathing intervals:
```
stabilityScore = 1 - (stdDev / mean).coerceIn(0, 0.5) / 0.5
```

#### 4. Progress Delta
Week-over-week improvement normalized to -1 to +1 scale.

### Level Classification

| BCI Score | Level | Description |
|-----------|-------|-------------|
| 0-20 | Beginner | Starting respiratory training |
| 20-40 | Developing | Building foundation |
| 40-60 | Moderate | Consistent practice |
| 60-80 | Good | Strong respiratory fitness |
| 80-100 | Excellent | Advanced capacity |

---

## Protocol Recommendation Algorithm

### Flow

1. **Safety Filter**
   - User level ≥ protocol minLevel
   - No matching contraindications
   - Advanced consent for requiring protocols
   - Level 3+ for hypoxic adaptation

2. **NSI Compatibility Filter**
   - HYPERAROUSAL: exclude sympathetic stimulation
   - HYPOAROUSAL: exclude parasympathetic (for low-level users)
   - BALANCED: all mechanisms allowed

3. **Heart Rate Safety**
   - HR > threshold: only allow relaxation protocols

4. **Relevance Scoring**
   - Category preference: +0.2
   - Time of day match: +0.1-0.15
   - Nervous state alignment: +0.15
   - Previous completion: +0.05
   - Difficulty match: +0.1

5. **Sort and Limit**
   - Sort by relevance score descending
   - Return top 5 for UI

---

## Safety Rules

### Automatic Restrictions

1. **Heart Rate Threshold (100 BPM)**
   - Auto-reduce hold duration to 15s
   - Show safety warning
   - Recommend relaxation only

2. **HRV Threshold (30ms)**
   - Warning about fatigue
   - Suggest lighter practice

3. **Wim Hof / Hypoxic Protocols**
   - Blocked if: hypertension OR HRV < 30 OR level < 3
   - Requires explicit consent with timestamp

4. **Hyperventilation Prevention**
   - Max frequency: 30 cycles/min
   - Auto-pause if 3 consecutive errors

5. **Emergency Protocol**
   - "Calm in 60 seconds" available at any time
   - Simple parasympathetic activation

---

## References

1. Laborde, S., et al. (2017). Heart Rate Variability and Cardiac Vagal Tone in Psychophysiological Research. *Frontiers in Psychology*, 8, 213.

2. Zaccaro, A., et al. (2018). How Breath-Control Can Change Your Life: A Systematic Review on Psycho-Physiological Correlates of Slow Breathing. *Frontiers in Human Neuroscience*, 12, 353.

3. Russo, M.A., et al. (2017). The physiological effects of slow breathing in the healthy human. *Breathe*, 13(4), 298-309.

4. Courtney, R. (2009). The functions of breathing and its dysfunctions and their relationship to breathing therapy. *International Journal of Osteopathic Medicine*, 12(3), 78-85.

5. Kox, M., et al. (2014). Voluntary activation of the sympathetic nervous system and attenuation of the innate immune response in humans. *PNAS*, 111(20), 7379-7384.
