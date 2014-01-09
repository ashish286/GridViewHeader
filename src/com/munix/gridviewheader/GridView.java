package com.munix.gridviewheader;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;
import android.widget.RelativeLayout;
import android.annotation.TargetApi;
import android.app.Activity;

/**
 * Componente de GridView con posibilidad de añadir un header mediante
 * addHeaderView(View v, Object data, boolean isSelectable);
 * addHeaderView(View v);
 * No funciona con ArrayAdapter sino con adapters extendidos de BaseAdapter
 * 
 * @author munix
 *
 */
public class GridView extends android.widget.GridView implements OnScrollListener
{
	private int mScrollOfsset;
	private int initialTopPadding=0;
	private int mDisplayWidth = 0;
	private int headerViewHeight = 0;
	private ListAdapter originalAdapter;
	private BaseAdapter newAdapter;
	private Map<Integer,Integer> adapterViewSizes = new HashMap<Integer,Integer>();
	private int lastPos=0;
	private OnScrollListener listenerFromActivity; 
	private FixedViewInfo mHeaderViewInfo;
	private Boolean setFixed=false;
	
	public GridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GridView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context)
	{
		super.setOnScrollListener(this);
	}
	
	@Override
	public void setOnScrollListener( OnScrollListener l )
	{
		listenerFromActivity = l;
		//Guardo la referencia del scroll para poder usar ambos
		super.setOnScrollListener(this);
	}
    
    @Override
    public void setAdapter( ListAdapter a )
    {
    	originalAdapter = a;
    	newAdapter = new mListAdapter();
    	super.setAdapter(newAdapter);
    }
    
    /**
     * Adaptador que recubre el original para poder dar espacio a la cabecera
     * @author munix
     *
     */
    public class mListAdapter extends BaseAdapter
    {
		@Override
		public int getCount() 
		{
			return originalAdapter.getCount() > 0 ? originalAdapter.getCount() + GridView.this.getNumColumnsCompat() : 0;
		}

		@Override
		public Object getItem(int position) 
		{
			return originalAdapter.getItem(position + GridView.this.getNumColumnsCompat());
		}

		@Override
		public long getItemId(int position) 
		{
			return originalAdapter.getItemId(position + GridView.this.getNumColumnsCompat());
		}

		class InternalViewHolder
		{
			View view;
		}
		
		@Override
		public View getView(int position, View convert, ViewGroup parent) 
		{
			if ( position < GridView.this.getNumColumnsCompat() )
			{
				if ( originalAdapter.getCount() > position )
				{
					convert = originalAdapter.getView(position, convert, parent);
				}else{
					convert = originalAdapter.getView(0, convert, parent);
				}
				ViewGroup.LayoutParams mParams = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, headerViewHeight );
				mParams.height = headerViewHeight;
				convert.setLayoutParams( mParams );
				convert.setVisibility(View.INVISIBLE);
			}else{
				int realPosition = position - GridView.this.getNumColumnsCompat();
				convert = originalAdapter.getView(realPosition, convert, parent);
				ViewGroup.LayoutParams mParams = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
		        mParams.height = measureViewHeightAtPosition(convert,getNumColumnsCompat());
		        convert.setLayoutParams( mParams );
		        convert.setVisibility(View.VISIBLE);
			}
			return convert;
		}
    };
    
    private int measureViewHeightAtPosition( View v, int position )
    {
    	if ( !adapterViewSizes.containsKey(position) )
    	{
    		v.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    		adapterViewSizes.put(position, v.getMeasuredHeight() );
    	}
    	return adapterViewSizes.get(position);	

    }
    
    /**
     * Añade la vista al layout
     * @param v la vista
     * @param data extra data
     * @param isSelectable foo
     */
	public void addHeaderView(View v, Object data, boolean isSelectable) 
	{
        mHeaderViewInfo = new ListView(getContext()).new FixedViewInfo();
        mHeaderViewInfo.view = v;
        mHeaderViewInfo.data = data;
        mHeaderViewInfo.isSelectable = isSelectable;

        setupView(v);
        
        int topPadding = this.getPaddingTop();
        if(initialTopPadding == 0){
        	initialTopPadding = topPadding;
        }
        headerViewHeight = v.getMeasuredHeight();
        
        RelativeLayout parent = (RelativeLayout)this.getParent();
        parent.addView(v, 0);
        v.bringToFront();
    }
	
	public FixedViewInfo getHeaderView()
	{
		return mHeaderViewInfo;
	}
    
    private void setupView(View v)
    {
    	boolean isLayedOut = !((v.getRight()==0) && (v.getLeft()==0) && (v.getTop()==0) && (v.getBottom()==0));
    	
    	if(v.getMeasuredHeight() != 0 && isLayedOut ) return;
    	
    	if(mDisplayWidth == 0){
    		DisplayMetrics displaymetrics = new DisplayMetrics();
        	((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    		mDisplayWidth = displaymetrics.widthPixels;
    		
    	}
    	v.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	v.measure(MeasureSpec.makeMeasureSpec(mDisplayWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    	v.layout(0, getTotalHeaderHeight(), v.getMeasuredWidth(), getTotalHeaderHeight() + v.getMeasuredHeight());
    }
    
    /**
     * Añade la vista al layout
     * @param v
     */
    public void addHeaderView(View v) {
    	this.addHeaderView(v, null, false);
    }

    /**
     * Permite cambiar entre una cabecera fija o scrollable
     * @param Boolean fixed
     */
    public void setFixedHeader( Boolean fixed )
    {
    	this.setFixed = fixed;
    }
    
    private void drawHeaders() 
    {
    	if ( mHeaderViewInfo != null )
    	{
	    	int startPos = -mScrollOfsset; 
	    	//Para evitar ciclos infinitos de onDraw / drawHeaders porque si en onDraw le pongo el topMargin efectúa
	    	//repintado, entonces llama a drawHeaders y así....
	    	if ( lastPos != startPos && !setFixed) 
	    	{
	    		if ( mScrollOfsset <= headerViewHeight )
	    		{
			    	RelativeLayout.LayoutParams mParams = (android.widget.RelativeLayout.LayoutParams)mHeaderViewInfo.view.getLayoutParams();
			    	mParams.topMargin = startPos;
			    	mHeaderViewInfo.view.setLayoutParams(mParams);
			    	mHeaderViewInfo.view.setVisibility( View.VISIBLE );
			    	Utilities.log("mHeaderViewInfo.view.setVisibility( View.VISIBLE );");
	    		}else{
	    			mHeaderViewInfo.view.setVisibility( View.GONE );
	    			Utilities.log("mHeaderViewInfo.view.setVisibility( View.GONE );");
	    		}
	    	}
	    	lastPos = startPos;
    	}
    }
    @Override
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);
    	if ( newAdapter != null )
    	{
    		drawHeaders();
    	}
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {        
    	super.dispatchDraw(canvas);
        
    }
    
    private int getTotalHeaderHeight()
    {
		return headerViewHeight;
    }
    
    private int getNumColumnsCompat() 
    {
        if (Build.VERSION.SDK_INT >= 11) {
            return getNumColumnsCompat11();

        } else {
            int columns = 0;
            int children = getChildCount();
            if (children > 0) {
                int width = getChildAt(0).getMeasuredWidth();
                if (width > 0) {
                    columns = getWidth() / width;
                }
            }
            return columns > 0 ? columns : AUTO_FIT;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private int getNumColumnsCompat11() {
        return getNumColumns();
    }
    
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
	{
		if(this.getAdapter()!=null){
			int count  = this.getChildCount();
			int totalHeaderHeight = getTotalHeaderHeight();
			
			if( count > this.getNumColumnsCompat() )
			{
				View child = this.getChildAt( this.getNumColumnsCompat() );

				mScrollOfsset = ((firstVisibleItem / this.getNumColumnsCompat()) * child.getMeasuredHeight()) + totalHeaderHeight - child.getTop();
			}
		}
		if ( listenerFromActivity != null )
		{
			listenerFromActivity.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
		
	}
	
	/**
	 * Elimina la cabecera
	 */
	public void removeHeaderView() 
	{
        if (mHeaderViewInfo != null) 
        {
        	RelativeLayout parent = (RelativeLayout)this.getParent();
            parent.removeView( mHeaderViewInfo.view );
            super.setAdapter(originalAdapter);
        }
    }

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) 
	{
		if ( listenerFromActivity != null )
		{
			listenerFromActivity.onScrollStateChanged(view, scrollState);
		}
	}
}
