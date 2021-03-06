package net.osmand.plus.views;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import net.osmand.plus.IconsCache;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;

/**
 * Created by Denis
 * on 10.12.2014.
 */
public class DirectionDrawable extends Drawable {
	Paint paintRouteDirection;
	float width;
	float height;
	Context ctx;
	private float angle;
	int resourceId = -1;
	Drawable arrowImage ;

	public DirectionDrawable(Context ctx, float width, float height, int resourceId, int clrId) {
		this(ctx, width, height);
		IconsCache iconsCache = ((OsmandApplication) ctx.getApplicationContext()).getIconsCache();
		arrowImage = iconsCache.getIcon(resourceId, clrId);
		this.resourceId = resourceId;
	}
	
	public void setImage(int resourceId, int clrId) {
		IconsCache iconsCache = ((OsmandApplication) ctx.getApplicationContext()).getIconsCache();
		arrowImage = iconsCache.getIcon(resourceId, clrId);
		this.resourceId = resourceId;
		onBoundsChange(getBounds());
	}

	public void setImage(int resourceId) {
		IconsCache iconsCache = ((OsmandApplication) ctx.getApplicationContext()).getIconsCache();
		arrowImage = iconsCache.getIcon(resourceId, 0);
		this.resourceId = resourceId;
		onBoundsChange(getBounds());
	}
	
	public DirectionDrawable(Context ctx, float width, float height) {
		this.ctx = ctx;
		this.width = width;
		this.height = height;
		paintRouteDirection = new Paint();
		paintRouteDirection.setStyle(Paint.Style.FILL_AND_STROKE);
		paintRouteDirection.setColor(ctx.getResources().getColor(R.color.color_unknown));
		paintRouteDirection.setAntiAlias(true);
	}

	public void setOpenedColor(int opened) {
		if(arrowImage != null) {
			IconsCache iconsCache = ((OsmandApplication) ctx.getApplicationContext()).getIconsCache();
			if (opened == 0) {
				arrowImage = iconsCache.getIcon(resourceId, R.color.color_ok);
			} else if (opened != -1) {
				arrowImage = iconsCache.getIcon(resourceId, R.color.color_warning);
			}
		}
		if (opened == 0) {
			paintRouteDirection.setColor(ctx.getResources().getColor(R.color.color_ok));
		} else if (opened == -1) {
			paintRouteDirection.setColor(ctx.getResources().getColor(R.color.color_unknown));
		} else {
			paintRouteDirection.setColor(ctx.getResources().getColor(R.color.color_warning));
		}
	}


	public void setAngle(float angle) {
		this.angle = angle;
	}
	

	@Override
	public int getIntrinsicWidth() {
		if (arrowImage != null) {
			return arrowImage.getIntrinsicWidth();
		}
		return super.getIntrinsicWidth();
	}
	
	@Override
	public int getIntrinsicHeight() {
		if (arrowImage != null) {
			return arrowImage.getIntrinsicHeight();
		}
		return super.getIntrinsicHeight();
	}
	
	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		if (arrowImage != null) {
			Rect r = getBounds();
			int w = arrowImage.getIntrinsicWidth();
			int h = arrowImage.getIntrinsicHeight();
			int dx = r.width() - w;
			int dy = r.height() - h;
			arrowImage.setBounds(r.left + dx / 2, r.top + dy / 2, r.right - dx / 2, r.bottom - dy / 2);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		if (arrowImage != null) {
			Rect r = getBounds();
			canvas.rotate(angle, r.centerX(), r.centerY());
			arrowImage.draw(canvas);
		} else {
			canvas.rotate(angle, canvas.getWidth() / 2, canvas.getHeight() / 2);
			Path directionPath = createDirectionPath();
			canvas.drawPath(directionPath, paintRouteDirection);
		}
		canvas.restore();
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		paintRouteDirection.setAlpha(alpha);

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paintRouteDirection.setColorFilter(cf);
	}

	private Path createDirectionPath() {
		int h = 15;
		int w = 4;
		float sarrowL = 8; // side of arrow
		float harrowL = (float) Math.sqrt(2) * sarrowL; // hypotenuse of arrow
		float hpartArrowL = (harrowL - w) / 2;
		Path path = new Path();
		path.moveTo(width / 2, height - (height - h) / 3);
		path.rMoveTo(w / 2, 0);
		path.rLineTo(0, -h);
		path.rLineTo(hpartArrowL, 0);
		path.rLineTo(-harrowL / 2, -harrowL / 2); // center
		path.rLineTo(-harrowL / 2, harrowL / 2);
		path.rLineTo(hpartArrowL, 0);
		path.rLineTo(0, h);

		DisplayMetrics dm = new DisplayMetrics();
		Matrix pathTransform = new Matrix();
		WindowManager mgr = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		mgr.getDefaultDisplay().getMetrics(dm);
		pathTransform.postScale(dm.density, dm.density);
		path.transform(pathTransform);
		width *= dm.density;
		height *= dm.density;
		return path;
	}

}
