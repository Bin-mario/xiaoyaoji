package cn.com.xiaoyaoji.view;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.View;

import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/8/9
 */
public class MultiView {
    /** View instance or view name String */
    private Object view;

    /** Model Map */
    private ModelMap model;

    /** Optional HTTP status for the response */
    private HttpStatus status;

    /** Indicates whether or not this instance has been cleared with a call to {@link #clear()} */
    private boolean cleared = false;


    /**
     * Default constructor for bean-style usage: populating bean
     * properties instead of passing in constructor arguments.
     * @see #setView(View)
     * @see #setViewName(String)
     */
    public MultiView() {
    }

    /**
     * Convenient constructor when there is no model data to expose.
     * Can also be used in conjunction with {@code addObject}.
     * @param viewName name of the View to render, to be resolved
     * by the DispatcherServlet's ViewResolver
     * @see #addObject
     */
    public MultiView(String viewName) {
        this.view = viewName;
    }

    /**
     * Convenient constructor when there is no model data to expose.
     * Can also be used in conjunction with {@code addObject}.
     * @param view View object to render
     * @see #addObject
     */
    public MultiView(View view) {
        this.view = view;
    }

    /**
     * Creates new MultiView given a view name and a model.
     * @param viewName name of the View to render, to be resolved
     * by the DispatcherServlet's ViewResolver
     * @param model Map of model names (Strings) to model objects
     * (Objects). Model entries may not be {@code null}, but the
     * model Map may be {@code null} if there is no model data.
     */
    public MultiView(String viewName, Map<String, ?> model) {
        this.view = viewName;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
    }

    /**
     * Creates new MultiView given a View object and a model.
     * <emphasis>Note: the supplied model data is copied into the internal
     * storage of this class. You should not consider to modify the supplied
     * Map after supplying it to this class</emphasis>
     * @param view View object to render
     * @param model Map of model names (Strings) to model objects
     * (Objects). Model entries may not be {@code null}, but the
     * model Map may be {@code null} if there is no model data.
     */
    public MultiView(View view, Map<String, ?> model) {
        this.view = view;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
    }

    /**
     * Creates new MultiView given a view name, model, and status.
     * @param viewName name of the View to render, to be resolved
     * by the DispatcherServlet's ViewResolver
     * @param model Map of model names (Strings) to model objects
     * (Objects). Model entries may not be {@code null}, but the
     * model Map may be {@code null} if there is no model data.
     * @param status an alternative status code to use for the response;
     * The response status is set just prior to View rendering.
     * @since 4.3
     */
    public MultiView(String viewName, Map<String, ?> model, HttpStatus status) {
        this.view = viewName;
        if (model != null) {
            getModelMap().addAllAttributes(model);
        }
        this.status = status;
    }

    /**
     * Convenient constructor to take a single model object.
     * @param viewName name of the View to render, to be resolved
     * by the DispatcherServlet's ViewResolver
     * @param modelName name of the single entry in the model
     * @param modelObject the single model object
     */
    public MultiView(String viewName, String modelName, Object modelObject) {
        this.view = viewName;
        addObject(modelName, modelObject);
    }

    /**
     * Convenient constructor to take a single model object.
     * @param view View object to render
     * @param modelName name of the single entry in the model
     * @param modelObject the single model object
     */
    public MultiView(View view, String modelName, Object modelObject) {
        this.view = view;
        addObject(modelName, modelObject);
    }


    /**
     * Set a view name for this MultiView, to be resolved by the
     * DispatcherServlet via a ViewResolver. Will override any
     * pre-existing view name or View.
     */
    public void setViewName(String viewName) {
        this.view = viewName;
    }

    /**
     * Return the view name to be resolved by the DispatcherServlet
     * via a ViewResolver, or {@code null} if we are using a View object.
     */
    public String getViewName() {
        return (this.view instanceof String ? (String) this.view : null);
    }

    /**
     * Set a View object for this MultiView. Will override any
     * pre-existing view name or View.
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Return the View object, or {@code null} if we are using a view name
     * to be resolved by the DispatcherServlet via a ViewResolver.
     */
    public View getView() {
        return (this.view instanceof View ? (View) this.view : null);
    }

    /**
     * Indicate whether or not this {@code MultiView} has a view, either
     * as a view name or as a direct {@link View} instance.
     */
    public boolean hasView() {
        return (this.view != null);
    }

    /**
     * Return whether we use a view reference, i.e. {@code true}
     * if the view has been specified via a name to be resolved by the
     * DispatcherServlet via a ViewResolver.
     */
    public boolean isReference() {
        return (this.view instanceof String);
    }

    /**
     * Return the model map. May return {@code null}.
     * Called by DispatcherServlet for evaluation of the model.
     */
    protected Map<String, Object> getModelInternal() {
        return this.model;
    }

    /**
     * Return the underlying {@code ModelMap} instance (never {@code null}).
     */
    public ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new ModelMap();
        }
        return this.model;
    }

    /**
     * Return the model map. Never returns {@code null}.
     * To be called by application code for modifying the model.
     */
    public Map<String, Object> getModel() {
        return getModelMap();
    }

    /**
     * Set the HTTP status to use for the response.
     * <p>The response status is set just prior to View rendering.
     * @since 4.3
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     * Return the configured HTTP status for the response, if any.
     * @since 4.3
     */
    public HttpStatus getStatus() {
        return this.status;
    }


    /**
     * Add an attribute to the model.
     * @param attributeName name of the object to add to the model
     * @param attributeValue object to add to the model (never {@code null})
     * @see ModelMap#addAttribute(String, Object)
     * @see #getModelMap()
     */
    public MultiView addObject(String attributeName, Object attributeValue) {
        getModelMap().addAttribute(attributeName, attributeValue);
        return this;
    }

    /**
     * Add an attribute to the model using parameter name generation.
     * @param attributeValue the object to add to the model (never {@code null})
     * @see ModelMap#addAttribute(Object)
     * @see #getModelMap()
     */
    public MultiView addObject(Object attributeValue) {
        getModelMap().addAttribute(attributeValue);
        return this;
    }

    /**
     * Add all attributes contained in the provided Map to the model.
     * @param modelMap a Map of attributeName -> attributeValue pairs
     * @see ModelMap#addAllAttributes(Map)
     * @see #getModelMap()
     */
    public MultiView addAllObjects(Map<String, ?> modelMap) {
        getModelMap().addAllAttributes(modelMap);
        return this;
    }


    /**
     * Clear the state of this MultiView object.
     * The object will be empty afterwards.
     * <p>Can be used to suppress rendering of a given MultiView object
     * in the {@code postHandle} method of a HandlerInterceptor.
     * @see #isEmpty()
     * @see HandlerInterceptor#postHandle
     */
    public void clear() {
        this.view = null;
        this.model = null;
        this.cleared = true;
    }

    /**
     * Return whether this MultiView object is empty,
     * i.e. whether it does not hold any view and does not contain a model.
     */
    public boolean isEmpty() {
        return (this.view == null && CollectionUtils.isEmpty(this.model));
    }

    /**
     * Return whether this MultiView object is empty as a result of a call to {@link #clear}
     * i.e. whether it does not hold any view and does not contain a model.
     * <p>Returns {@code false} if any additional state was added to the instance
     * <strong>after</strong> the call to {@link #clear}.
     * @see #clear()
     */
    public boolean wasCleared() {
        return (this.cleared && isEmpty());
    }


    /**
     * Return diagnostic information about this model and view.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MultiView: ");
        if (isReference()) {
            sb.append("reference to view with name '").append(this.view).append("'");
        }
        else {
            sb.append("materialized View is [").append(this.view).append(']');
        }
        sb.append("; model is ").append(this.model);
        return sb.toString();
    }
}
