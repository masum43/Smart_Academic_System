package com.github.bkhezry.searchablespinner;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.github.bkhezry.searchablespinner.interfaces.ISpinnerSelectedView;
import com.github.bkhezry.searchablespinner.interfaces.IStatusListener;
import com.github.bkhezry.searchablespinner.interfaces.OnItemSelectedListener;
import com.github.bkhezry.searchablespinner.tools.EditCursorColor;
import com.github.bkhezry.searchablespinner.tools.UITools;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.widget.IconTextView;

import gr.escsoft.michaelprimez.searchablespinner.R;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import io.codetail.widget.RevealLinearLayout;

public class SearchableSpinner extends RelativeLayout implements View.OnClickListener {

  private static final int DefaultElevation = 16;
  private static final int DefaultAnimationDuration = 300;
  private ViewState mViewState = ViewState.ShowingRevealedLayout;
  private IStatusListener mStatusListener;
  private CardView mRevealContainerCardView;
  private LinearLayout mRevealItem;
  private IconTextView mStartSearchImageView;

  private CardView mContainerCardView;
  private AppCompatEditText mSearchEditText;
  private IconTextView mDoneSearchImageView;
  private RevealLinearLayout mSpinnerListContainer;
  private PopupWindow mPopupWindow;
  private ListView mSpinnerListView;
  private TextView mEmptyTextView;

  private Context mContext;
  private OnItemSelectedListener mOnItemSelected;
  private SelectedView mCurrSelectedView;

  /* Attributes */
  private @ColorInt
  int mRevealViewBackgroundColor;
  private @ColorInt
  int mStartEditTintColor;
  private @ColorInt
  int mEditViewBackgroundColor;
  private @ColorInt
  int mEditViewTextColor;
  private @ColorInt
  int mDoneEditTintColor;
  private @ColorInt
  int mBoarderColor;
  private Drawable mListItemDivider;
  private @Px
  int mBordersSize;
  private @Px
  int mExpandSize;
  private @Px
  int mListDividerSize;
  private boolean mShowBorders;
  private boolean mKeepLastSearch;
  private String mRevealEmptyText;
  private String mSearchHintText;
  private String mNoItemsFoundText;
  private int mAnimDuration;
  private String mFontName;
  private Typeface mTypeface;
  private TextView revealEmptyText;
  private boolean isSpinnerOpen = false;
  private static final int PADDING_DP = 15;
  private boolean isSearchable = true;
  private RevealFrameLayout revealFrameLayout;
  private @ColorInt
  int spinnerBorderColor;

  public enum ViewState {
    ShowingRevealedLayout,
    ShowingEditLayout,
    ShowingAnimation
  }

  static {
    Iconify.with(new MaterialModule());
  }

  public SearchableSpinner(@NonNull Context context) {
    this(context, null);
  }

  public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    getAttributeSet(attrs, defStyleAttr, defStyleRes);

    final LayoutInflater factory = LayoutInflater.from(context);
    factory.inflate(R.layout.view_searchable_spinner, this, true);

    mSpinnerListContainer = (RevealLinearLayout) factory.inflate(R.layout.view_list, this, false);
    mSpinnerListView = mSpinnerListContainer.findViewById(R.id.spinner_list_view);
    if (mListItemDivider != null) {
      mSpinnerListView.setDivider(mListItemDivider);
      mSpinnerListView.setDividerHeight(mListDividerSize);
    }
    mEmptyTextView = mSpinnerListContainer.findViewById(R.id.empty_text_view);
    mSpinnerListView.setEmptyView(mEmptyTextView);
    if (mFontName != null) {
      createTypeface();
    }
  }

  private void getAttributeSet(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
    if (attrs != null) {
      try {
        TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SearchableSpinner, defStyleAttr, defStyleRes);
        mRevealViewBackgroundColor = attributes.getColor(R.styleable.SearchableSpinner_reveal_view_background_color, Color.WHITE);
        mStartEditTintColor = attributes.getColor(R.styleable.SearchableSpinner_start_search_tint_color, Color.GRAY);
        mEditViewBackgroundColor = attributes.getColor(R.styleable.SearchableSpinner_search_view_background_color, Color.WHITE);
        mEditViewTextColor = attributes.getColor(R.styleable.SearchableSpinner_search_view_text_color, Color.BLACK);
        mDoneEditTintColor = attributes.getColor(R.styleable.SearchableSpinner_done_search_tint_color, Color.GRAY);
        mBordersSize = attributes.getDimensionPixelSize(R.styleable.SearchableSpinner_borders_size, 4);
        mExpandSize = attributes.getDimensionPixelSize(R.styleable.SearchableSpinner_spinner_expand_height, 0);
        mShowBorders = attributes.getBoolean(R.styleable.SearchableSpinner_show_borders, false);
        mBoarderColor = attributes.getColor(R.styleable.SearchableSpinner_boarder_color, Color.GRAY);
        mAnimDuration = attributes.getColor(R.styleable.SearchableSpinner_anim_duration, DefaultAnimationDuration);
        mKeepLastSearch = attributes.getBoolean(R.styleable.SearchableSpinner_keep_last_search, false);
        mRevealEmptyText = attributes.getString(R.styleable.SearchableSpinner_reveal_empty_text);
        mSearchHintText = attributes.getString(R.styleable.SearchableSpinner_search_hint_text);
        mNoItemsFoundText = attributes.getString(R.styleable.SearchableSpinner_no_items_found_text);
        mListItemDivider = attributes.getDrawable(R.styleable.SearchableSpinner_items_divider);
        mListDividerSize = attributes.getDimensionPixelSize(R.styleable.SearchableSpinner_divider_height, 0);
        mFontName = attributes.getString(R.styleable.SearchableSpinner_font_name);
        isSearchable = attributes.getBoolean(R.styleable.SearchableSpinner_searchable, true);
        spinnerBorderColor = attributes.getColor(R.styleable.SearchableSpinner_spinner_border_color, Color.TRANSPARENT);
      } catch (UnsupportedOperationException e) {
        Log.e("SearchableSpinner", "getAttributeSet --> " + e.getLocalizedMessage());
      }
    }
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mRevealContainerCardView = findViewById(R.id.reveal_container_card_view);
    mRevealContainerCardView.setOnClickListener(mOnRevelViewClickListener);
    mRevealItem = findViewById(R.id.selected_item_layout);
    mStartSearchImageView = findViewById(R.id.start_search_icon_text_view);

    mContainerCardView = findViewById(R.id.container_card_view);
    mSearchEditText = findViewById(R.id.search_edit_text);
    mDoneSearchImageView = findViewById(R.id.done_search_icon_text_view);
    revealFrameLayout = findViewById(R.id.reveal_frame_layout_container);
    init();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    getScreenSize();
    int width = MeasureSpec.getSize(widthMeasureSpec);
    if (mShowBorders) {     // + 4 because of card layout_margin in the view_searchable_spinner.xml
      width -= UITools.dpToPx(mContext, (mBordersSize + 4));
    } else {
      width -= UITools.dpToPx(mContext, 8);
    }
    mPopupWindow.setWidth(width);
    if (mExpandSize <= 0) {
      mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    } else {
      mPopupWindow.setHeight(heightMeasureSpec);
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    super.onLayout(changed, l, t, r, b);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    requestLayout();
    super.onScrollChanged(l, t, oldl, oldt);
  }

  private void init() {
    setupColors();
    setupList();
    mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
    if (!isSearchable) {
      mSearchEditText.setVisibility(INVISIBLE);
    }
    revealFrameLayout.setBackgroundColor(spinnerBorderColor);
    mStartSearchImageView.setOnClickListener(this);
    mDoneSearchImageView.setOnClickListener(this);
    mSearchEditText.addTextChangedListener(mTextWatcher);

    mPopupWindow = new PopupWindow(mContext);
    mPopupWindow.setContentView(mSpinnerListContainer);
    mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
    mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        hideEdit();
      }
    });
    mPopupWindow.setFocusable(false);
    if (UITools.isLollipopOrHigher()) {
      mPopupWindow.setElevation(DefaultElevation);
    }
    mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.spinner_drawable));

    mSpinnerListView.setOnItemClickListener(mOnItemSelectedListener);
    if (mCurrSelectedView == null) {
      if (!TextUtils.isEmpty(mSearchHintText)) {
        mSearchEditText.setHint(mSearchHintText);
      }
      if (!TextUtils.isEmpty(mNoItemsFoundText)) {
        mEmptyTextView.setText(mNoItemsFoundText);
      }
      if (mCurrSelectedView == null && !TextUtils.isEmpty(mRevealEmptyText)) {
        revealEmptyText = new TextView(mContext);
        revealEmptyText.setText(mRevealEmptyText);
        float density = mContext.getResources().getDisplayMetrics().density;
        int paddingPixel = (int) (PADDING_DP * density);
        revealEmptyText.setPadding(0, 0, paddingPixel, 0);
        if (mTypeface != null) {
          revealEmptyText.setTypeface(mTypeface);
        }
        mCurrSelectedView = new SelectedView(revealEmptyText, -1, 0);
        mRevealItem.addView(revealEmptyText);
      }
    } else {
      mSpinnerListView.performItemClick(mCurrSelectedView.getView(), mCurrSelectedView.getPosition(), mCurrSelectedView.getId());
    }
    clearAnimation();
    clearFocus();
  }

  private AdapterView.OnItemClickListener mOnItemSelectedListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (mCurrSelectedView == null) {
        Adapter adapter = parent.getAdapter();
        if (adapter instanceof ISpinnerSelectedView) {
          View selectedView = ((ISpinnerSelectedView) adapter).getSelectedView(position);
          mCurrSelectedView = new SelectedView(selectedView, position, selectedView.getId());
        } else {
          mCurrSelectedView = new SelectedView(view, position, id);
        }
        mSpinnerListView.setSelection(position);
      } else {
        Adapter adapter = parent.getAdapter();
        if (adapter instanceof ISpinnerSelectedView) {
          View selectedView = ((ISpinnerSelectedView) adapter).getSelectedView(position);
          mCurrSelectedView = new SelectedView(selectedView, position, selectedView.getId());
        } else {
          mCurrSelectedView.setView(view);
          mCurrSelectedView.setPosition(position);
          mCurrSelectedView.setId(id);
        }
        mSpinnerListView.setSelection(position);
      }
      if (mCurrSelectedView == null) {
        if (mOnItemSelected != null) {
          mOnItemSelected.onNothingSelected();
        }
      } else if (mCurrSelectedView != null) {
        mRevealItem.removeAllViews();
        mSpinnerListView.removeViewInLayout(mCurrSelectedView.getView());
        mRevealItem.addView(mCurrSelectedView.getView());
        ((BaseAdapter) mSpinnerListView.getAdapter()).notifyDataSetChanged();
        if (mOnItemSelected != null) {
          mOnItemSelected.onItemSelected(mCurrSelectedView.getView(), mCurrSelectedView.getPosition(), mCurrSelectedView.getId());
        }
      }
      hideEdit();
    }
  };

  public Object getSelectedItem() {
    if (mCurrSelectedView != null) {
      int position = mCurrSelectedView.getPosition();
      Adapter adapter = mSpinnerListView.getAdapter();
      if (adapter != null && adapter.getCount() > 0 && position >= 0) {
        return adapter.getItem(position);
      } else {
        return null;
      }
    }
    return null;
  }

  public void setSelectedItem(int position) {
    Adapter adapter = mSpinnerListView.getAdapter();
    if (adapter instanceof ISpinnerSelectedView) {
      View selectedView = ((ISpinnerSelectedView) adapter).getSelectedView(position);
      mCurrSelectedView = new SelectedView(selectedView, position, selectedView.getId());
      mSpinnerListView.setSelection(position);
    } else {
      TextView textView = new TextView(mContext);
      textView.setText(mRevealEmptyText);
      mCurrSelectedView = new SelectedView(textView, -1, 0);
      mRevealItem.addView(textView);
    }
    if (mCurrSelectedView == null) {
      if (mOnItemSelected != null) {
        mOnItemSelected.onNothingSelected();
      }
    } else if (mCurrSelectedView != null) {
      mRevealItem.removeAllViews();
      mSpinnerListView.removeViewInLayout(mCurrSelectedView.getView());
      mRevealItem.addView(mCurrSelectedView.getView());
      ((BaseAdapter) mSpinnerListView.getAdapter()).notifyDataSetChanged();
      if (mOnItemSelected != null) {
        mOnItemSelected.onItemSelected(mCurrSelectedView.getView(), mCurrSelectedView.getPosition(), mCurrSelectedView.getId());
      }
    }
    hideEdit();
  }

  public int getItemPosition(Object item) {
    if (item == null) {
      return -1;
    }
    Adapter adapter = mSpinnerListView.getAdapter();
    if (adapter != null) {
      for (int i = 0; i < adapter.getCount(); i++) {
        Object adpItem = adapter.getItem(i);
        if (adpItem != null && adpItem.equals(item)) {
          return i;
        }
      }
    }
    return -1;
  }

  public void setSelectedItem(Object item) {
    int itemPosition = getItemPosition(item);
    if (itemPosition >= 0) {
      setSelectedItem(itemPosition);
    }
  }

  public int getSelectedPosition() {
    if (mCurrSelectedView != null) {
      return mCurrSelectedView.getPosition();
    }
    return -1;
  }

  private OnClickListener mOnRevelViewClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (mViewState == ViewState.ShowingRevealedLayout) {
        getRootView().post(new Runnable() {
          @Override
          public void run() {
            revealEditView();
          }
        });
      } else if (mViewState == ViewState.ShowingEditLayout) {
        hideEditView();
      }
    }
  };

  private TextWatcher mTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      Filterable filterable = (Filterable) mSpinnerListView.getAdapter();
      if (filterable != null) {
        filterable.getFilter().filter(s);
      }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  private void setupColors() {
    mRevealContainerCardView.setBackgroundColor(mRevealViewBackgroundColor);
    mRevealItem.setBackgroundColor(mRevealViewBackgroundColor);
    mStartSearchImageView.setBackgroundColor(mRevealViewBackgroundColor);
    mStartSearchImageView.setTextColor(mStartEditTintColor);

    mContainerCardView.setBackgroundColor(mEditViewBackgroundColor);
    mSearchEditText.setBackgroundColor(mEditViewBackgroundColor);
    mSearchEditText.setTextColor(mEditViewTextColor);
    mSearchEditText.setHintTextColor(mStartEditTintColor);
    EditCursorColor.setCursorColor(mSearchEditText, mEditViewTextColor);
    mDoneSearchImageView.setBackgroundColor(mEditViewBackgroundColor);
    mDoneSearchImageView.setTextColor(mDoneEditTintColor);
  }

  private void setupList() {
    MarginLayoutParams spinnerListViewLayoutParams = (MarginLayoutParams) mSpinnerListView.getLayoutParams();
    ViewGroup.LayoutParams spinnerListContainerLayoutParams = mSpinnerListContainer.getLayoutParams();
    LinearLayout.LayoutParams listLayoutParams = (LinearLayout.LayoutParams) mSpinnerListView.getLayoutParams();

    spinnerListContainerLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

    if (mExpandSize <= 0) {
      listLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    } else {
      listLayoutParams.height = mExpandSize;
    }
    mSpinnerListContainer.setBackgroundColor(mBoarderColor);
    if (mShowBorders && mBordersSize > 0) {
      spinnerListViewLayoutParams.setMargins(mBordersSize, mBordersSize, mBordersSize, mBordersSize);
    } else {
      spinnerListViewLayoutParams.setMargins(0, 0, 0, 0);
    }
  }

  public void setAdapter(ListAdapter adapter) {
    if (!(adapter instanceof Filterable)) {
      throw new IllegalArgumentException("Adapter should implement the Filterable interface");
    }
    mSpinnerListView.setAdapter(adapter);
  }

  public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
    mOnItemSelected = onItemSelectedListener;
  }

  @Override
  protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    hideEdit();
    getScreenSize();
  }

  private void getScreenSize() {
    DisplayMetrics metrics = new DisplayMetrics();
    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(metrics);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.start_search_icon_text_view) {
      revealEdit();
    } else if (id == R.id.done_search_icon_text_view) {
      hideEdit();
    }
  }

  public void revealEdit() {
    if (mViewState == ViewState.ShowingRevealedLayout) {
      if (!mKeepLastSearch) {
        mSearchEditText.setText(null);
      }
      revealEditView();
    }
  }

  public void hideEdit() {
    if (mViewState == ViewState.ShowingEditLayout) {
      hideEditView();
    }
  }

  private void revealEditView() {
    mViewState = ViewState.ShowingAnimation;
    if (mStatusListener != null) {
      mStatusListener.spinnerIsOpening();
      isSpinnerOpen = true;
    }
    final int cx = mRevealContainerCardView.getLeft();
    final int cxr = mRevealContainerCardView.getRight();
    final int cy = (mRevealContainerCardView.getTop() + mRevealContainerCardView.getHeight()) / 2;
    final int reverseStartRadius = Math.max(mRevealContainerCardView.getWidth(), mRevealContainerCardView.getHeight());
    final int reverseEndRadius = 0;

    if (!mPopupWindow.isShowing()) {
      mPopupWindow.showAsDropDown(this, cx, 0);
    }
    mPopupWindow.getContentView().post(new Runnable() {
      @Override
      public void run() {
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mPopupWindow.getContentView().findViewById(R.id.search_list_layout), cxr, cy, reverseEndRadius, reverseStartRadius);
        revealAnimator.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            mViewState = ViewState.ShowingEditLayout;
            mRevealContainerCardView.setVisibility(View.INVISIBLE);
          }

          @Override
          public void onAnimationCancel(Animator animation) {

          }

          @Override
          public void onAnimationRepeat(Animator animation) {

          }
        });
        revealAnimator.setDuration(mAnimDuration);
        revealAnimator.start();
      }
    });

    final Animator animator = ViewAnimationUtils.createCircularReveal(mContainerCardView, cxr, cy, reverseEndRadius, reverseStartRadius);
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mContainerCardView.setVisibility(View.VISIBLE);
        mViewState = ViewState.ShowingEditLayout;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    mSpinnerListContainer.setVisibility(View.VISIBLE);
    mContainerCardView.setVisibility(View.VISIBLE);

    animator.setDuration(mAnimDuration);

    animator.start();

    mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        mPopupWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        final Animator spinnerListContainerAnimator = ViewAnimationUtils.createCircularReveal(mPopupWindow.getContentView().findViewById(R.id.search_list_layout), cxr, cy, reverseEndRadius, reverseStartRadius);
        spinnerListContainerAnimator.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {

          }

          @Override
          public void onAnimationEnd(Animator animation) {
            mSpinnerListContainer.setVisibility(View.VISIBLE);
          }

          @Override
          public void onAnimationCancel(Animator animation) {

          }

          @Override
          public void onAnimationRepeat(Animator animation) {

          }
        });
        spinnerListContainerAnimator.setDuration(mAnimDuration);
        spinnerListContainerAnimator.start();
      }
    });

  }

  private void hideEditView() {
    mViewState = ViewState.ShowingAnimation;
    if (mStatusListener != null) {
      mStatusListener.spinnerIsClosing();
      isSpinnerOpen = false;
    }
    final int cx = mContainerCardView.getLeft();
    final int cxr = mContainerCardView.getRight();
    final int cy = (mContainerCardView.getTop() + mContainerCardView.getHeight()) / 2;
    final int reverseStartRadius = Math.max(mContainerCardView.getWidth(), mContainerCardView.getHeight());
    final int reverseEndRadius = 0;

    final Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mRevealContainerCardView, cx, cy, reverseEndRadius, reverseStartRadius);
    revealAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mViewState = ViewState.ShowingRevealedLayout;
        mRevealContainerCardView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    final Animator cardViewAnimator = ViewAnimationUtils.createCircularReveal(mContainerCardView, cxr, cy, reverseStartRadius, reverseEndRadius);
    cardViewAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mContainerCardView.setVisibility(View.INVISIBLE);
        mViewState = ViewState.ShowingRevealedLayout;
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    mRevealContainerCardView.setVisibility(View.VISIBLE);
    cardViewAnimator.setDuration(mAnimDuration);
    cardViewAnimator.start();

    revealAnimator.setDuration(mAnimDuration);
    revealAnimator.start();

    if (mPopupWindow.isShowing()) {
      final Animator spinnerListContainerAnimator = ViewAnimationUtils.createCircularReveal(mPopupWindow.getContentView().findViewById(R.id.search_list_layout), cxr, cy, reverseStartRadius, reverseEndRadius);
      spinnerListContainerAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mSpinnerListContainer.setVisibility(View.GONE);
          mPopupWindow.dismiss();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      });
      spinnerListContainerAnimator.setDuration(mAnimDuration);
      spinnerListContainerAnimator.start();
    }
  }

  public boolean isInsideSearchEditText(MotionEvent event) {
    Rect editTextRect = new Rect();
    mSearchEditText.getHitRect(editTextRect);
    return editTextRect.contains((int) event.getX(), (int) event.getY());
  }

  public void setStatusListener(IStatusListener statusListener) {
    mStatusListener = statusListener;
  }

  public void setFontName(String fontName) {
    this.mFontName = fontName;
    if (fontName != null) {
      createTypeface();
      revealEmptyText.setTypeface(mTypeface);
    }
  }

  public void setTypeFace(Typeface typeFace) {
    this.mTypeface = typeFace;
    if (mTypeface != null) {
      revealEmptyText.setTypeface(mTypeface);
    }
  }

  private void createTypeface() {
    mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + mFontName);
  }

  public boolean isSpinnerOpen() {
    return isSpinnerOpen;
  }

  public void setSpinnerBorderColor(@ColorInt int color) {
    this.spinnerBorderColor = color;
    revealFrameLayout.setBackgroundColor(color);
  }

  public void setSearchable(boolean isSearchable) {
    this.isSearchable = isSearchable;
    if (!isSearchable) {
      mSearchEditText.setVisibility(INVISIBLE);
    }
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.mViewState = ViewState.ShowingRevealedLayout;
    ss.mAnimDuration = mAnimDuration;
    ss.mBordersSize = mBordersSize;
    ss.mExpandSize = mExpandSize;
    ss.mBoarderColor = mBoarderColor;
    ss.mRevealViewBackgroundColor = mRevealViewBackgroundColor;
    ss.mStartEditTintColor = mStartEditTintColor;
    ss.mEditViewBackgroundColor = mEditViewBackgroundColor;
    ss.mEditViewTextColor = mEditViewTextColor;
    ss.mDoneEditTintColor = mDoneEditTintColor;
    ss.mShowBorders = mShowBorders;
    ss.mKeepLastSearch = mKeepLastSearch;
    ss.mRevealEmptyText = mRevealEmptyText;
    ss.mSearchHintText = mSearchHintText;
    ss.mNoItemsFoundText = mNoItemsFoundText;
    ss.mSelectedViewPosition = mCurrSelectedView != null ? mCurrSelectedView.getPosition() : -1;
    ss.mFontName = mFontName;
    return ss;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }
    SavedState ss = (SavedState) state;
    mViewState = ss.mViewState;
    mAnimDuration = ss.mAnimDuration;
    mBordersSize = ss.mBordersSize;
    mExpandSize = ss.mExpandSize;
    mBoarderColor = ss.mBoarderColor;
    mRevealViewBackgroundColor = ss.mRevealViewBackgroundColor;
    mStartEditTintColor = ss.mStartEditTintColor;
    mEditViewBackgroundColor = ss.mEditViewBackgroundColor;
    mEditViewTextColor = ss.mEditViewTextColor;
    mDoneEditTintColor = ss.mDoneEditTintColor;
    mShowBorders = ss.mShowBorders;
    mKeepLastSearch = ss.mKeepLastSearch;
    mRevealEmptyText = ss.mRevealEmptyText;
    mSearchHintText = ss.mSearchHintText;
    mNoItemsFoundText = ss.mNoItemsFoundText;
    int mSelectedViewPosition = ss.mSelectedViewPosition;
    mFontName = ss.mFontName;

    if (mSelectedViewPosition >= 0) {
      View v = mSpinnerListView.getAdapter().getView(mSelectedViewPosition, null, null);
      mSpinnerListView.performItemClick(v, mSelectedViewPosition, v.getId());
    }
    if (mFontName != null) {
      createTypeface();
    }
    super.onRestoreInstanceState(ss.getSuperState());
  }

  static class SavedState extends BaseSavedState {
    ViewState mViewState;
    int mAnimDuration;
    @Px
    int mBordersSize;
    @Px
    int mExpandSize;
    @ColorInt
    int mBoarderColor;
    @ColorInt
    int mRevealViewBackgroundColor;
    @ColorInt
    int mStartEditTintColor;
    @ColorInt
    int mEditViewBackgroundColor;
    @ColorInt
    int mEditViewTextColor;
    @ColorInt
    int mDoneEditTintColor;
    boolean mShowBorders;
    boolean mKeepLastSearch;
    String mRevealEmptyText;
    String mSearchHintText;
    String mNoItemsFoundText;
    int mSelectedViewPosition;
    String mFontName;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      mViewState = ViewState.values()[in.readInt()];
      mAnimDuration = in.readInt();
      mBordersSize = in.readInt();
      mExpandSize = in.readInt();
      mBoarderColor = in.readInt();
      mRevealViewBackgroundColor = in.readInt();
      mStartEditTintColor = in.readInt();
      mEditViewBackgroundColor = in.readInt();
      mEditViewTextColor = in.readInt();
      mDoneEditTintColor = in.readInt();
      mShowBorders = in.readInt() > 0;
      mKeepLastSearch = in.readInt() > 0;
      mRevealEmptyText = in.readString();
      mSearchHintText = in.readString();
      mNoItemsFoundText = in.readString();
      mSelectedViewPosition = in.readInt();
      mFontName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(mViewState.ordinal());
      out.writeInt(mAnimDuration);
      out.writeInt(mBordersSize);
      out.writeInt(mExpandSize);
      out.writeInt(mBoarderColor);
      out.writeInt(mRevealViewBackgroundColor);
      out.writeInt(mStartEditTintColor);
      out.writeInt(mEditViewBackgroundColor);
      out.writeInt(mEditViewTextColor);
      out.writeInt(mDoneEditTintColor);
      out.writeInt(mShowBorders ? 1 : 0);
      out.writeInt(mKeepLastSearch ? 1 : 0);
      out.writeString(mRevealEmptyText);
      out.writeString(mSearchHintText);
      out.writeString(mNoItemsFoundText);
      out.writeInt(mSelectedViewPosition);
      out.writeString(mFontName);
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }
}
