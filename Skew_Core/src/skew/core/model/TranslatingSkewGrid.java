package skew.core.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TranslatingSkewGrid<T> implements ISkewGrid<T>
{

	private ISkewGrid<T> backing;
	private int dx, dy;
	
	public TranslatingSkewGrid(ISkewGrid<T> backing, int dx, int dy) {
		this.backing = backing;
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setTranslation(int x, int y)
	{
		this.dx = x;
		this.dy = y;
	}
	
	public int size() {
		return backing.size();
	}

	public int getWidth() {
		return backing.getWidth();
	}

	public int getHeight() {
		return backing.getHeight();
	}

	@Override
	public ISkewPoint<T> getPoint(int position) {
		int y = position / getWidth();
		int x = position % getWidth();
		return getPoint(x, y);
	}

	@Override
	public ISkewPoint<T> getPoint(int x, int y) {
		if (x+dx < 0 || y+dy < 0   ||   x+dx >= getWidth() || y+dy >= getHeight()) {
			return new SkewPoint<T>( x+dx, y+dy, (x+dx) + (y+dy)*getWidth(), getOutOfBoundsPoint() );
		}
		return new SkewPoint<T>(x, y, x+y*getWidth(), backing.getData(x+dx, y+dy));
	}

	@Override
	public List<ISkewPoint<T>> getPoints() {
		List<ISkewPoint<T>> points = new ArrayList<>();
		for (int i = 0; i < getWidth()*getHeight(); i++) {
			points.add(getPoint(i));
		}
		return points;
	}

	@Override
	public T getData(int position) {
		return getPoint(position).getData();
	}

	@Override
	public T getData(int x, int y) {
		return getPoint(x, y).getData();
	}

	
	protected abstract T getOutOfBoundsPoint();
	
}
