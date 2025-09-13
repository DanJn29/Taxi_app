# Test Examples for Enhanced Formatting

## Departure Time Test Cases

### Input: `"2025-09-10T06:19:00+00:00"`
Expected Output: "Երկուշաբթի, 10 սեպ 06:19-ին" (or "Այսօր 06:19-ին" if today)

### Color Test Cases

### Input: `"#10b981"`
Expected Output: "կանաչ" (green)

### Other Color Examples:
- `"#FFFFFF"` → "սպիտակ" (white)
- `"#000000"` → "սև" (black)  
- `"#FF0000"` → "կարմիր" (red)
- `"#0000FF"` → "կապույտ" (blue)
- `"#FFFF00"` → "դեղին" (yellow)

## Enhanced Trip Card Display

The ClientHomeScreen trip cards will now show:

**Before:**
- Departure: `"2025-09-10T06:19:00+00:00"`
- Color: `"#10b981"`

**After:**
- Departure: `"Մեկնում՝ Երկուշաբթի, 10 սեպ 06:19-ին"`
- Color: `"Համար՝ ABC123 • կանաչ"`

This provides users with immediately understandable information in Armenian.
