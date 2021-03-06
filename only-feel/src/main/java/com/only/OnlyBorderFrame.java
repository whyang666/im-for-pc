/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.only;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.only.box.OnlyImageBox;
import com.only.component.BorderDialog;

/**
 * 
 * @author XiaHui
 */
@SuppressWarnings("serial")
public class OnlyBorderFrame extends OnlyFrame {

	private BorderDialog borderDialog;
	private int borderDialogX;
	private int borderDialogY;
	private int borderDialogW;
	private int borderDialogH;
	private ImageIcon border_r = OnlyImageBox.getClassPathImageIconByName("window/window_border.png");
	private MouseInputListener mouseInputListener = new MouseInputHandler();

	public OnlyBorderFrame() {
		super();
		initBaseFrame();
	}

	public OnlyBorderFrame(GraphicsConfiguration gc) {
		super(gc);
		initBaseFrame();
	}

	public OnlyBorderFrame(String title) {
		super(title);
		initBaseFrame();
	}

	public OnlyBorderFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		initBaseFrame();
	}

	private void initBaseFrame() {
		this.borderDialog = new BorderDialog(this);
		this.setBackground(new Color(230, 230, 230));
		borderDialog.setBorderImage(border_r.getImage());
		borderDialog.addMouseListener(mouseInputListener);
		borderDialog.addMouseMotionListener(mouseInputListener);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowIconified(java.awt.event.WindowEvent evt) {
				formWindowIconified(evt);
			}

			@Override
			public void windowDeiconified(java.awt.event.WindowEvent evt) {
				formWindowDeiconified(evt);
			}
		});
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized(evt);
			}

			@Override
			public void componentMoved(java.awt.event.ComponentEvent evt) {
				formComponentMoved(evt);
			}
		});

		addWindowStateListener(new java.awt.event.WindowStateListener() {
			@Override
			public void windowStateChanged(java.awt.event.WindowEvent evt) {
				formWindowStateChanged(evt);
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		if(Frame.ICONIFIED!=this.getExtendedState()){
			borderDialog.setVisible(b);
		}
		super.setVisible(b);
	}

	private void formComponentMoved(java.awt.event.ComponentEvent evt) {
		formResized();
	}

	private void formComponentResized(java.awt.event.ComponentEvent evt) {
		formResized();
		formComponentResized();
	}

	private void formWindowDeiconified(java.awt.event.WindowEvent evt) {
		setVisible(true);
	}

	private void formWindowIconified(java.awt.event.WindowEvent evt) {
		borderDialog.setVisible(false);
	}

	private void formWindowStateChanged(java.awt.event.WindowEvent evt) {
		formResized();
		formComponentResized();
	}

	public void formResized() {
		// borderDialog.setAlwaysOnTop(true);
		borderDialogX = this.getX() - borderDialog.getBorderX();
		borderDialogY = this.getY() - borderDialog.getBorderY();
		borderDialogW = this.getWidth() + (borderDialog.getBorderX() * 2);
		borderDialogH = this.getHeight() + (borderDialog.getBorderY() * 2);
		borderDialog.setBounds(borderDialogX - 1, borderDialogY - 1, borderDialogW + 1, borderDialogH + 1);
	}

	public void formComponentResized() {
	}

	class MouseInputHandler implements MouseInputListener {

		private final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		private final int CORNER_DRAG_WIDTH = 10;
		private final int BORDER_DRAG_THICKNESS = 5;
		private final int[] CURSOR_MAPPING = { Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0, Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0,
				Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, 0, 0, 0, Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };
		private boolean isMovingWindow;
		private boolean dragging;
		private int dragCursor;
		private int dragOffsetX;
		private int dragOffsetY;
		private int dragWidth;
		private int dragHeight;

		@Override
		public void mousePressed(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}

			Point dragWindowOffset = e.getPoint();
			//Window win = (Window) e.getSource();
			dragging = true;

			

			Frame frame = OnlyBorderFrame.this;
			frame.toFront();

			int frameState = (frame != null) ? frame.getExtendedState() : 0;

			if (((frame != null && (frameState & Frame.MAXIMIZED_BOTH) == 0))
					&& (dragWindowOffset.y >= BORDER_DRAG_THICKNESS && dragWindowOffset.y < frame.getHeight() - BORDER_DRAG_THICKNESS && dragWindowOffset.x >= BORDER_DRAG_THICKNESS && dragWindowOffset.x < frame.getWidth() - BORDER_DRAG_THICKNESS)) {
				isMovingWindow = true;
				dragOffsetX = dragWindowOffset.x;
				dragOffsetY = dragWindowOffset.y;
			}

			if (!isMovingWindow && ((frame != null && frame.isResizable() && (frameState & Frame.MAXIMIZED_BOTH) == 0))) {
				dragOffsetX = dragWindowOffset.x;
				dragOffsetY = dragWindowOffset.y;
				dragWidth = frame.getWidth();
				dragHeight = frame.getHeight();
				dragCursor = getCursor(calculateCorner(frame, dragWindowOffset.x, dragWindowOffset.y));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}

			if (dragCursor != 0 && OnlyBorderFrame.this != null && !OnlyBorderFrame.this.isValid()) {
				OnlyBorderFrame.this.validate();
				OnlyBorderFrame.this.getRootPane().repaint();
			}

			dragging = false;
			isMovingWindow = false;
			dragCursor = 0;
			mouseMoved(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Window win = (Window) e.getSource();
			Frame frame = OnlyBorderFrame.this;

			int cursor = getCursor(calculateCorner(win, e.getX(), e.getY()));

			if (cursor != 0 && win.getBounds().contains(e.getLocationOnScreen()) && ((frame != null && (frame.isResizable() && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0)))) {
				win.setCursor(Cursor.getPredefinedCursor(cursor));
			} else {
				win.setCursor(DEFAULT_CURSOR);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!dragging) {
				Window win = (Window) e.getSource();
				win.setCursor(DEFAULT_CURSOR);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				if (OnlyBorderFrame.this.getBounds().contains(e.getPoint())) {
					mouseMoved(e);
				} else {
					mouseExited(e);
				}
				return;
			}

			//Window win = (Window) e.getSource();
			Point point = e.getPoint();

			if (isMovingWindow) {
				//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Point eventLocationOnScreen = e.getLocationOnScreen();
				int x = eventLocationOnScreen.x - dragOffsetX;
				int y = eventLocationOnScreen.y - dragOffsetY;
				OnlyBorderFrame.this.setLocation(x, y);
			} else if (dragCursor != 0) {
				Rectangle rect = OnlyBorderFrame.this.getBounds();
				Rectangle startBounds = new Rectangle(rect);
				Dimension min = OnlyBorderFrame.this.getMinimumSize();
				Dimension max = OnlyBorderFrame.this.getMaximumSize();

				switch (dragCursor) {
				case Cursor.E_RESIZE_CURSOR: {
					adjust(rect, min, max, 0, 0, point.x + (dragWidth - dragOffsetX) - rect.width, 0);
					break;
				}
				case Cursor.S_RESIZE_CURSOR: {
					adjust(rect, min, max, 0, 0, 0, point.y + (dragHeight - dragOffsetY) - rect.height);
					break;
				}
				case Cursor.N_RESIZE_CURSOR: {
					adjust(rect, min, max, 0, point.y - dragOffsetY, 0, -(point.y - dragOffsetY));
					break;
				}
				case Cursor.W_RESIZE_CURSOR: {
					adjust(rect, min, max, point.x - dragOffsetX, 0, -(point.x - dragOffsetX), 0);
					break;
				}
				case Cursor.NE_RESIZE_CURSOR: {
					adjust(rect, min, max, 0, point.y - dragOffsetY, point.x + (dragWidth - dragOffsetX) - rect.width, -(point.y - dragOffsetY));
					break;
				}
				case Cursor.SE_RESIZE_CURSOR: {
					adjust(rect, min, max, 0, 0, point.x + (dragWidth - dragOffsetX) - rect.width, point.y + (dragHeight - dragOffsetY) - rect.height);
					break;
				}
				case Cursor.NW_RESIZE_CURSOR: {
					adjust(rect, min, max, point.x - dragOffsetX, point.y - dragOffsetY, -(point.x - dragOffsetX), -(point.y - dragOffsetY));
					break;
				}
				case Cursor.SW_RESIZE_CURSOR: {
					adjust(rect, min, max, point.x - dragOffsetX, 0, -(point.x - dragOffsetX), point.y + (dragHeight - dragOffsetY) - rect.height);
					break;
				}
				default: {
					break;
				}
				}

				if (!rect.equals(startBounds)) {
					OnlyBorderFrame.this.setBounds(rect);

					if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
						OnlyBorderFrame.this.validate();
						OnlyBorderFrame.this.getRootPane().repaint();
					}
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
		}

		private void adjust(Rectangle bounds, Dimension min, Dimension max, int deltaX, int deltaY, int deltaWidth, int deltaHeight) {
			bounds.x += deltaX;
			bounds.y += deltaY;
			bounds.width += deltaWidth;
			bounds.height += deltaHeight;

			if (min != null) {
				if (bounds.width < min.width) {
					int correction = min.width - bounds.width;

					if (deltaX != 0) {
						bounds.x -= correction;
					}

					bounds.width = min.width;
				}

				if (bounds.height < min.height) {
					int correction = min.height - bounds.height;

					if (deltaY != 0) {
						bounds.y -= correction;
					}

					bounds.height = min.height;
				}
			}

			if (max != null) {
				if (bounds.width > max.width) {
					int correction = max.width - bounds.width;

					if (deltaX != 0) {
						bounds.x -= correction;
					}

					bounds.width = max.width;
				}

				if (bounds.height > max.height) {
					int correction = max.height - bounds.height;

					if (deltaY != 0) {
						bounds.y -= correction;
					}

					bounds.height = max.height;
				}
			}
		}

		private int calculateCorner(Window win, int x, int y) {
			Insets insets = win.getInsets();
			int xPosition = calculatePosition(x - insets.left, win.getWidth() - insets.left - insets.right);
			int yPosition = calculatePosition(y - insets.top, win.getHeight() - insets.top - insets.bottom);

			if (xPosition == -1 || yPosition == -1) {
				return -1;
			}

			return yPosition * 5 + xPosition;
		}

		private int getCursor(int corner) {
			if (corner == -1) {
				return 0;
			}

			return CURSOR_MAPPING[corner];
		}

		private int calculatePosition(int spot, int width) {
			if (spot < BORDER_DRAG_THICKNESS) {
				return 0;
			} else if (spot < CORNER_DRAG_WIDTH) {
				return 1;
			} else if (spot >= (width - BORDER_DRAG_THICKNESS)) {
				return 4;
			} else if (spot >= (width - CORNER_DRAG_WIDTH)) {
				return 3;
			} else {
				return 2;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}
}
