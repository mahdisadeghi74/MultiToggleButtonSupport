package com.dependencies.buria.android.support.multitogglebutton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static android.content.ContentValues.TAG;

public class MultiToggleButton extends LinearLayout {
    // typeface value
    private final int TEXT_STYLE_NORMAL = 0;
    private final int TEXT_STYLE_BOLD = 1;
    private final int TEXT_STYLE_ITALIC = 2;


    //attrs put user
    private ArrayList<Integer> toggleDrawables = null;
    private ArrayList<String> toggleStrings = null;
    private String text = "";

    Float buttonPadding = 0f;
    int toggleButtonTint = 0;
    Float textSize = 12f;
    ColorStateList textColor = null;
    private int textStyle = TEXT_STYLE_NORMAL;
    float toggleButtonSize = 24f;

    // variables counter
    private int currentItem = 0;
    private int itemCount = 0;
    private boolean isText = false;

    // views
    private ImageButton tgb;
    private TextView tvTgb;
    // listener
    private OnDrawableChangeListener onItemChangeListener = null;
    private OnTextChangeListener onTextChangeListener = null;

    private Context context;

    public MultiToggleButton(Context context) {
        super(context);
    }

    public MultiToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs, 0);
    }

    public MultiToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs, defStyleAttr);
    }

    public MultiToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {
        textSize = toDP(context, 12f);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null)
            inflater.inflate(R.layout.layout_multi_toggle_button, this);

        tvTgb = findViewById(R.id.tvTgb);
        tgb = findViewById(R.id.tgb);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MultiToggleButton);


        try {
            text = typeArray.getString(R.styleable.MultiToggleButton_text);
            buttonPadding = typeArray.getDimension(R.styleable.MultiToggleButton_buttonPadding, toDP(context, 8f));
            toggleButtonTint =
                    typeArray.getResourceId(R.styleable.MultiToggleButton_toggleButtonTint, 0);
            textSize = typeArray.getDimension(R.styleable.MultiToggleButton_textSize, toDP(context, 14f));
            textColor = typeArray.getColorStateList(R.styleable.MultiToggleButton_textColor);
            textStyle = typeArray.getInt(R.styleable.MultiToggleButton_textStyle, TEXT_STYLE_NORMAL);
            toggleButtonSize = typeArray.getDimensionPixelSize(R.styleable.MultiToggleButton_toggleButtonSize, Math.round(toDP(context, 24f)));

            if (tvTgb != null) {
                tvTgb.setText(text);
                tvTgb.setPadding(buttonPadding.intValue(), 0, 0, 0);
                if (textColor != null)
                    tvTgb.setTextColor(textColor);
                tvTgb.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

                if (textStyle != 0) {
                    switch (textStyle) {
                        case TEXT_STYLE_BOLD:
                            tvTgb.setTypeface(Typeface.DEFAULT_BOLD);
                            break;
                        case TEXT_STYLE_ITALIC:
                            tvTgb.setTypeface(tvTgb.getTypeface(), Typeface.ITALIC);
                            break;
                        default:
                            tvTgb.setTypeface(Typeface.DEFAULT);
                            break;
                    }
                }
            }

            if (tgb != null) {
                if (toggleButtonTint != 0)
                    tgb.setImageTintList(context.getResources().getColorStateList(toggleButtonTint));

                tgb.getLayoutParams().height = Math.round(toggleButtonSize);
                tgb.getLayoutParams().width = Math.round(toggleButtonSize);
            }
        } finally {
            typeArray.recycle();
        }

        Log.d(TAG, "init: isText: " + (isText ? "true" : "false"));

        tgb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToggleImageResource();
                setCurrentImageResource(context);

                if (onTextChangeListener != null) {
                    onTextChangeListener.onItemChangeListener(getCurrentText(),
                            currentItem);
                }
            }
        });


        tvTgb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tgb.callOnClick();
            }
        });

    }

    public void addToggleItems(int... resources) {
        isText = false;
        if (toggleDrawables == null) {
            toggleDrawables = new ArrayList<>();
        }

        if (resources != null) {
            for (int r : resources) {
                toggleDrawables.add(r);
            }
            itemCount = toggleDrawables.size();
            setCurrentImageResource(context);
        }
    }

    public void addToggleItems(String... resources) {
        isText = true;
        toggleButtonSize = 0f;

        if (toggleStrings == null) {
            toggleStrings = new ArrayList<>();
        }

        if (resources != null) {
            toggleStrings.addAll(Arrays.asList(resources));
            itemCount = toggleStrings.size();
            setCurrentImageResource(context);
        }
    }

    public void addToggleItems(ArrayList<?> resources) {
        if (toggleDrawables == null) {
            toggleDrawables = new ArrayList<>();
        }
        if (toggleStrings == null) {
            toggleStrings = new ArrayList<>();
        }
        if (resources != null) {
            if (resources.size() > 0) {
                if (resources.get(0) instanceof Integer) {
                    isText = false;
                    toggleDrawables.addAll((Collection<? extends Integer>) resources);
                    itemCount = toggleDrawables.size();
                    setCurrentImageResource(context);
                } else if (resources.get(0) instanceof String) {
                    isText = true;
                    toggleButtonSize = 0f;

                    toggleStrings.addAll((Collection<? extends String>) resources);
                    itemCount = toggleStrings.size();
                    setCurrentImageResource(context);
                }
            }

        }
    }


    private void setCurrentImageResource(Context context) {
        if (itemCount > 0) {
            if (!isText) {
                if (toggleDrawables != null) {
                    if (currentItem < itemCount) {
                        if (toggleDrawables.get(currentItem) != null) {
                            try {
                                String resource = context.getResources().getResourceName(toggleDrawables.get(currentItem));
                                if (resource != null)
                                    tgb.setImageDrawable(context.getResources().getDrawable(toggleDrawables.get(currentItem)));
                            }catch (Resources.NotFoundException e){
                                itemCount = 0;
                                currentItem = 0;
                            }
                        }
                    }
                }
            } else {
                if (toggleStrings != null) {
                    if (currentItem < itemCount) {
                        if (toggleStrings.get(currentItem) != null) {
                            tvTgb.setText(toggleStrings.get(currentItem));
                        }
                    }
                }
            }
        }
    }

    private void setToggleImageResource() {
        currentItem = (currentItem == itemCount - 1) ? 0 : currentItem + 1;
    }

    private int getCurrentResource() {
        if (toggleDrawables != null) {
            if (currentItem < itemCount) {
                return toggleDrawables.get(currentItem);
            }
        }
        return 0;
    }

    private String getCurrentText() {
        if (toggleStrings != null) {
            if (currentItem < itemCount) {
                return toggleStrings.get(currentItem);
            }
        }
        return text;
    }

    public void setOnItemChangeListener(OnDrawableChangeListener onDrawableChangeListener) {
        this.onItemChangeListener = onDrawableChangeListener;
    }

    public void setOnItemChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public int getCurrentItem() {
        return getCurrentResource();
    }


    private Float toDP(Context context, Float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics()
        );
    }


    public interface OnDrawableChangeListener {
        void onItemChangeListener(int resourceId, int position);
    }

    public interface OnTextChangeListener {
        void onItemChangeListener(String text, int position);
    }

    public void setDefaultPosition(int position) {
        if (position >= 0 && position < itemCount) {
            currentItem = position;
            setCurrentImageResource(context);
        }
    }


    public void setDefaultItem(int drawable) {
        if (!isText) {
            if (toggleDrawables != null) {
                int index = toggleDrawables.indexOf(drawable);
                setDefaultPosition(index);
                setCurrentImageResource(context);
            }
        }
    }


    public void setDefaultItem(String text){
        if (isText) {
            if (toggleStrings != null) {
                int index = toggleStrings.indexOf(text);
                setDefaultPosition(index);
                setCurrentImageResource(context);
            }
        }
    }

    public void setText(String text){
        tvTgb.setText(text);
    }
}
