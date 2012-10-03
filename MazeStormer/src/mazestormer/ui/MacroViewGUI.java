package mazestormer.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import mazestormer.facade.IFacade;
import mazestormer.model.BoardModel;

/**
 * A class with the graphical user interface for the board.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class MacroViewGUI extends JPanel{

	private static final long serialVersionUID = -5412856771873196193L;
	private final static int TILE_SIZE = 50;
	
	private final MacroViewPanel mvp;
	private boolean showGrid = true;
	
	private int prePressOriginX = 0;
	private int prePressOriginY = 0;
	private int clickX;
	private int clickY;
	private int originX;
	private int originY;
	
	private Image robotImage;
	private Image wallImage;
	private Color backGroundColor;
	private Color borderColor;
	
	public MacroViewGUI(final MacroViewPanel mvp){
		this.mvp = mvp;
		this.backGroundColor = Color.WHITE;
		this.borderColor = Color.BLACK;
		
		try {
			robotImage = ImageIO.read(getClass().getClassLoader().getResource("res/images/Android_Black 1.jpg"));
			wallImage = ImageIO.read(getClass().getClassLoader().getResource("res/images/wood2.jpg"));
		} catch(IOException e) {
			System.out.println("error reading images");
			System.exit(ERROR);
		}
		
		this.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseReleased(MouseEvent e){
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				MacroViewGUI.this.clickX = e.getX();
				MacroViewGUI.this.clickY = e.getY();
				MacroViewGUI.this.prePressOriginX = MacroViewGUI.this.originX;
				MacroViewGUI.this.prePressOriginY = MacroViewGUI.this.originY;
			}
			
			@Override
			public void mouseExited(MouseEvent e){
			}
			
			@Override
			public void mouseEntered(MouseEvent e){
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
			}
			
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
				int x = (int) ((-originX + point.getX()) / (TILE_SIZE + 1));
				int y = (int) ((-originY + point.getY()) / (TILE_SIZE + 1));
				IFacade facade = mvp.getFacade();
				for(BoardModel model : facade.getAllBoardModelsClass(mvp.getBoard(), BoardModel.class)){
					long xr, yr;
					xr = facade.getBoardModelX(model);
					yr = facade.getBoardModelY(model);
					if(xr == x && yr == y){
						mvp.setModelStatus(model.toString());
						return;
					}
				}
				mvp.setModelStatus("");
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				MacroViewGUI.this.originX = MacroViewGUI.this.prePressOriginX - (clickX - e.getX());
				MacroViewGUI.this.originY = MacroViewGUI.this.prePressOriginY - (clickY - e.getY());
				repaint();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(this.backGroundColor);
		g.fillRect(0, 0, width, height);
		// mark (0, 0)
		g.setColor(this.borderColor);
		g.drawString("(0, 0)", originX + 9, originY + 30);
		if(showGrid) {
			// draw vertical grid lines
			for(int x = originX % (TILE_SIZE + 1); x < width; x+=TILE_SIZE + 1) {
				g.drawLine(x, 0, x, height - 1);
			}
			//draw horizontal grid lines
			for(int y = originY % (TILE_SIZE + 1); y < height; y+=TILE_SIZE + 1) {
				g.drawLine(0, y, width - 1, y);
			}
		}
		
		IFacade facade = this.mvp.getFacade();
		for(BoardModel model : facade.getAllBoardModelsClass(this.mvp.getBoard(), BoardModel.class)) {
			long x = facade.getBoardModelX(model);
			long y = facade.getBoardModelY(model);
			if(x < Integer.MIN_VALUE + 2 * TILE_SIZE || x > Integer.MAX_VALUE - 2 * TILE_SIZE || y < Integer.MIN_VALUE + 2 * TILE_SIZE || y > Integer.MAX_VALUE - 2 * TILE_SIZE)
				continue;
			int tileXRoot = (int) (originX + x * (TILE_SIZE + 1) + 1);
			int tileYRoot = (int) (originY + y * (TILE_SIZE + 1) + 1);
			// draw item
			g.drawImage(getImage(model), tileXRoot, tileYRoot, null);
			// draw orientation
			if(mazestormer.model.Robot.class.isInstance(model)){
				paintOrientation(g, this.mvp.getFacade().getOrientation((mazestormer.model.Robot) model), tileXRoot, tileYRoot, Color.RED);
			}
		}	
	}
	
	private void paintOrientation(Graphics g, int orientation, int tileXRoot, int tileYRoot, Color color)
			throws NullPointerException{
		if(g == null || color == null)
			throw new NullPointerException("The given arguments may not refer the null reference.");
		int[] xPoints;
		int[] yPoints;
		int orientationXRoot = tileXRoot + TILE_SIZE / 2 - 3;
		int orientationYRoot = tileYRoot + TILE_SIZE / 2 + 8;
		if(orientation == 0) {
			xPoints = new int[] { orientationXRoot - 6, orientationXRoot, orientationXRoot + 6 };
			yPoints = new int[] {orientationYRoot , orientationYRoot - 6, orientationYRoot };
		} else if(orientation == 1) {
			xPoints = new int[] { orientationXRoot, orientationXRoot + 6, orientationXRoot };
			yPoints = new int[] {orientationYRoot - 6, orientationYRoot, orientationYRoot + 6 };
		} else if(orientation == 2) {
			xPoints = new int[] { orientationXRoot - 6, orientationXRoot, orientationXRoot + 6 };
			yPoints = new int[] {orientationYRoot , orientationYRoot + 6, orientationYRoot };
		} else {
			xPoints = new int[] { orientationXRoot, orientationXRoot - 6, orientationXRoot };
			yPoints = new int[] {orientationYRoot - 6, orientationYRoot, orientationYRoot + 6 };
		}
		g.setColor(color);
		g.fillPolygon(xPoints, yPoints, 3);
		g.setColor(Color.BLACK);
	}
	
	Image getImage(BoardModel model){
		if(mazestormer.model.Robot.class == model.getClass())
			return robotImage;
		else if(mazestormer.model.Wall.class == model.getClass())	
			return wallImage;
		return null;
	}
	
	Icon getIcon(BoardModel model){
		return new ImageIcon(getImage(model));
	}
	
	Icon getIcon(Class<? extends BoardModel> clazz){
		try {
			return new ImageIcon(getImage(((BoardModel) clazz.newInstance())));
		}
		catch(InstantiationException e){
			e.printStackTrace();
			return null;
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	void setBorderColor(Color c){
		if(c != null){
			this.borderColor = c;
			this.repaint();
		}
	}
	
	void setBackgroundColor(Color c){
		if(c != null){
			this.backGroundColor = c;
			this.repaint();
		}
	}
}
