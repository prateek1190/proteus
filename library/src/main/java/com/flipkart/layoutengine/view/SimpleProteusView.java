package com.flipkart.layoutengine.view;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.toolbox.Styles;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ProteusView} implementation built using a
 * {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {

    protected ProteusView parent;
    protected JsonObject layout;
    protected View view;
    protected int index;
    protected List<ProteusView> children;
    protected Styles styles;

    public SimpleProteusView(View view, int index, ProteusView parent) {
        this.view = view;
        this.index = index;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public SimpleProteusView(View view, JsonObject layout, int index, List<ProteusView> children, ProteusView parent) {
        this.view = view;
        this.layout = layout;
        this.index = index;
        this.parent = parent;
        this.children = children;
    }

    public SimpleProteusView(View view, JsonObject layout, int index, ProteusView parent) {
        this.view = view;
        this.layout = layout;
        this.index = index;
        this.parent = parent;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public ProteusView getParent() {
        return this.parent;
    }

    @Override
    public void unsetParent() {
        this.parent = null;
    }

    @Override
    public void addView(ProteusView child, int index) {
        if (child == null || child.getView() == null || this.view == null) {
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        if (index < 0 || index >= this.children.size()) {
            index = this.children.size();
            this.setIndex(index);
        }
        this.children.add(index, child);
        ((ViewGroup) view).addView(child.getView(), index);
    }

    @Override
    public void addView(ProteusView child) {
        addView(child, -1);
    }

    @Override
    public List<ProteusView> getChildren() {
        return this.children;
    }

    @Override
    public void replaceView(ProteusView child) {
        if (child.getView() == null) {
            return;
        }

        // remove the parent if the child view already has one
        if (child.getView().getParent() != null) {
            ((ViewGroup) child.getView().getParent()).removeView(child.getView());
        }

        if (parent != null && parent.getView() != null) {
            ProteusView oldChild = parent.removeView(index);
            if (oldChild != null ) {
                oldChild.destroy();
            }
            parent.addView(child, index);
        } else {
            ViewGroup parentView = (ViewGroup) this.view.getParent();
            if (parentView == null) {
                return;
            }
            int index = parentView.indexOfChild(this.view);
            parentView.removeView(this.view);
            parentView.addView(child.getView());
            if (index < parent.getChildren().size()) {
                parent.getChildren().add(index, child);
            } else {
                parent.getChildren().add(child);
            }
        }
        this.children = child.getChildren();
        this.layout = child.getLayout();
        this.styles = child.getStyles();
    }

    @Override
    public void removeView() {
        destroy();
    }

    @Override
    public ProteusView removeView(int childIndex) {
        ViewGroup group = (ViewGroup) view;
        if (childIndex < group.getChildCount()) {
            group.removeViewAt(childIndex);
        }
        if (childIndex < this.children.size()) {
            ProteusView child = children.remove(childIndex);
            child.unsetParent();
            return child;
        }
        return null;
    }

    public void removeView(ProteusView child) {
        if (child != null) {
            int index = this.children.indexOf(child);
            if (index >= 0) {
                this.children.remove(index);
            }
            if (child.getView() != null) {
                index = ((ViewGroup)this.view).indexOfChild(child.getView());
                if (index >= 0) {
                    ((ViewGroup)this.view).removeViewAt(index);
                }
            }
        }
    }

    @Override
    public JsonObject getLayout() {
        return layout;
    }

    @Override
    public void setStyles(Styles styles) {
        this.styles = styles;
    }

    @Override
    public Styles getStyles() {
        return styles;
    }

    @Override
    public View updateData(JsonObject data) {
        return updateDataImpl(data);
    }

    protected View updateDataImpl(JsonObject data) {
        return this.view;
    }

    @Override
    public void destroy() {
        if (parent != null && parent.getView() != null && view != null) {
            parent.removeView(index);
            parent = null;
        }
        view = null;
        children = null;
        layout = null;
        styles = null;
    }
}
