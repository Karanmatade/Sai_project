# All Rooms Screen - Futuristic Glassmorphism Redesign

## 🎨 Color Palette

### Primary Colors
- **Neon Cyan**: `#6BC5D6` - Primary accent, search highlights, available status
- **Neon Blue**: `#7A9CC6` - Secondary accent, edit buttons
- **Neon Purple**: `#A78BFA` - Tertiary accent, room numbers, price text
- **Neon Pink**: `#D4A5C7` - Booked status, delete buttons

### Background Colors
- **Dark Space**: `#0F1419` - Deep background
- **Dark Space Lighter**: `#1A1F26` - Gradient start
- **Gradient End**: `#151A20` - Gradient end

### Glassmorphism Colors
- **Glass White Strong**: `#30FFFFFF` - Premium glass cards
- **Glass White Medium**: `#20FFFFFF` - Standard glass cards
- **Glass White Light**: `#15FFFFFF` - Subtle glass effects

### Text Colors
- **Text Primary**: `#E8EDF2` - Main text
- **Text Secondary**: `#9CA8B4` - Secondary text, labels
- **Text Glow**: `#9BC5D6` - Accent text, headings

### Status Colors
- **Success Green**: `#6BC5A0` - Available rooms
- **Warning Orange**: `#D4A574` - Booked rooms accent
- **Chip Available BG**: `#1E3A2E` - Available chip background
- **Chip Booked BG**: `#3A2E2E` - Booked chip background

## 🎬 Animation Specifications

### Entry Animations

#### Search Bar Entry
- **Type**: Fade + Slide Down
- **Duration**: 500ms
- **Interpolator**: OvershootInterpolator(1.1f)
- **Properties**: 
  - Alpha: 0 → 1
  - TranslationY: -30dp → 0dp

#### Room Card Entry
- **Type**: Fade + Scale + Slide Up
- **Duration**: 450ms
- **Interpolator**: OvershootInterpolator(1.2f)
- **Properties**:
  - Alpha: 0 → 1
  - Scale: 0.9 → 1.0
  - TranslationY: 40dp → 0dp
- **Stagger**: Cards appear sequentially with slight delay

### Interactive Animations

#### Filter Chip Selection
- **Type**: Scale + Glow
- **Duration**: 250ms (scale up) + 150ms (scale down)
- **Interpolator**: OvershootInterpolator(1.2f)
- **Properties**:
  - Scale: 1.0 → 1.08 → 1.0
  - Elevation: 0dp → 8dp
  - Background: Inactive → Active glow

#### Status Badge Change
- **Type**: Scale Pulse
- **Duration**: 200ms (scale up) + 150ms (scale down)
- **Interpolator**: OvershootInterpolator()
- **Properties**:
  - Scale: 1.0 → 1.1 → 1.0

#### Button Click (Edit/Delete)
- **Type**: Scale Bounce
- **Duration**: 120ms (press) + 100ms (bounce) + 80ms (settle)
- **Interpolator**: AccelerateDecelerateInterpolator → OvershootInterpolator
- **Properties**:
  - Scale: 1.0 → 0.85 → 1.1 → 1.0
  - Ripple effect on MaterialCardView

#### Card Hover
- **Type**: Scale + Elevation
- **Duration**: 200ms
- **Properties**:
  - Scale: 1.0 → 1.02
  - Elevation: 20dp → 24dp

### Background Animations

#### Animated Gradient Circles
- **Type**: Orbital Movement
- **Duration**: 8000ms (Circle 1), 10000ms (Circle 2)
- **Interpolator**: AccelerateDecelerateInterpolator
- **Properties**:
  - TranslationX: 0 → ±50dp (repeating)
  - TranslationY: 0 → ±35dp (repeating)
  - Alpha: 0.15 (Circle 1), 0.1 (Circle 2)
- **Mode**: Infinite reverse loop

## 🎯 Layout Specifications

### Spacing
- **Card Padding**: 24dp (internal)
- **Card Margin**: 16dp (between cards)
- **Section Spacing**: 20dp
- **Element Spacing**: 16dp-20dp

### Corner Radius
- **Search Bar**: 24dp
- **Search Input**: 20dp
- **Room Cards**: 24dp
- **Filter Chips**: 20dp
- **Status Badges**: 18dp
- **Action Buttons**: 30dp (circular)

### Elevation
- **Search Bar**: 24dp
- **Room Cards**: 20dp (default), 24dp (hover)
- **Filter Chips**: 8dp (active), 0dp (inactive)
- **Action Buttons**: 16dp
- **Status Badges**: 8dp

### Typography
- **Font Family**: sans-serif-medium (Poppins/Inter style)
- **Room Number**: 36sp, Bold, Letter Spacing 0.05
- **Room Type**: 15sp, Medium, Alpha 0.85
- **Price**: 28sp, Bold, Letter Spacing 0.02
- **Price Label**: 12sp, Medium, Alpha 0.75
- **Filter Label**: 13sp, Bold, Letter Spacing 0.05
- **Filter Chips**: 14sp, Bold

## ✨ Visual Effects

### Glassmorphism
- **Frosted Blur**: Semi-transparent backgrounds with gradient overlays
- **Border Glow**: 2dp stroke with neon accent colors
- **Depth Layers**: Multiple elevation levels for depth perception

### Gradient Effects
- **Room Number**: Cyan (#6BC5D6) → Purple (#A78BFA)
- **Price**: Purple (#A78BFA) → Pink (#D4A5C7)
- **Borders**: Dynamic gradient borders based on room status

### Glow Effects
- **Available Rooms**: Cyan glow (#6BC5D6) with 70% opacity
- **Booked Rooms**: Pink glow (#D4A5C7) with 70% opacity
- **Active Filters**: Cyan glow with scale animation
- **Action Buttons**: Gradient glow with ripple effect

## 📱 Responsive Design

### Screen Sizes
- **Small**: Cards maintain 16dp margins, text scales appropriately
- **Medium**: Optimal spacing and sizing
- **Large**: Additional padding, larger cards

### Orientation
- **Portrait**: Vertical list with sticky search bar
- **Landscape**: Optimized spacing, maintains readability

## 🚀 Performance Optimizations

1. **RecyclerView**: Efficient list rendering with DiffUtil
2. **Animations**: Hardware-accelerated view animations
3. **Drawables**: Cached gradient drawables
4. **Background**: Lightweight animated circles
5. **Text Rendering**: Gradient shaders applied efficiently

## 🎨 Design Principles

1. **Consistency**: Unified color palette and spacing throughout
2. **Hierarchy**: Clear visual hierarchy with size, color, and elevation
3. **Feedback**: Immediate visual feedback on all interactions
4. **Accessibility**: High contrast ratios, readable font sizes
5. **Modern**: Glassmorphism, neon accents, smooth animations

