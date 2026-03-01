package com.qc.common.ui.fragment;

import android.util.Log;

import com.qc.common.self.CommonData;
import com.qc.mycomic.R;

import java.util.ArrayList;

import the.one.base.ui.fragment.BaseFragment;
import the.one.base.ui.fragment.BaseHomeFragment;

/**
 * @author LuQiChuang
 * @desc HOME界面
 * @date 2020/8/12 15:26
 * @ver 1.0
 */
public class MyHomeFragment extends BaseHomeFragment {

    private static final String TAG = "MyHomeFragment";

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MyHomeFragment onCreate called");
    }

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        Log.d(TAG, "MyHomeFragment onCreateView called");
        try {
            android.view.View view = super.onCreateView(inflater, container, savedInstanceState);
            Log.d(TAG, "MyHomeFragment onCreateView completed, view: " + (view != null ? "not null" : "null"));
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            throw e;
        }
    }

    @Override
    public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {
        Log.d(TAG, "MyHomeFragment onViewCreated called, view: " + (view != null ? "not null" : "null"));
        try {
            super.onViewCreated(view, savedInstanceState);
            Log.d(TAG, "MyHomeFragment onViewCreated completed");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            throw e;
        }
    }

    @Override
    protected void initView(android.view.View rootView) {
        Log.d(TAG, "MyHomeFragment initView called, rootView: " + (rootView != null ? "not null" : "null"));
        try {
            // 在 super.initView() 之前先初始化所有需要的 ArrayList 字段
            // 然后调用 addTabs 和 addFragment
            Log.d(TAG, "Initializing ArrayList fields before super.initView()");
            try {
                // 通过反射初始化所有 ArrayList 字段
                Class<?> currentClass = MyHomeFragment.this.getClass();
                while (currentClass != null && currentClass != Object.class) {
                    java.lang.reflect.Field[] fields = currentClass.getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        if (field.getType() == ArrayList.class) {
                            try {
                                field.setAccessible(true);
                                Object value = field.get(MyHomeFragment.this);
                                if (value == null) {
                                    ArrayList<?> newList = new ArrayList<>();
                                    field.set(MyHomeFragment.this, newList);
                                    Log.d(TAG, "Initialized ArrayList field: " + field.getName() + " in class " + currentClass.getSimpleName());
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Error initializing field " + field.getName(), e);
                            }
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                }
                
                // 现在可以安全地调用 addTabs 和 addFragment
                addTabs();
                Log.d(TAG, "addTabs() called before super.initView()");
                
                // 通过反射获取 fragments 字段并调用 addFragment
                try {
                    java.lang.reflect.Field fragmentsField = null;
                    currentClass = MyHomeFragment.this.getClass();
                    while (currentClass != null && currentClass != Object.class) {
                        try {
                            fragmentsField = currentClass.getDeclaredField("fragments");
                            fragmentsField.setAccessible(true);
                            ArrayList<BaseFragment> fragmentsList = (ArrayList<BaseFragment>) fragmentsField.get(MyHomeFragment.this);
                            if (fragmentsList == null) {
                                fragmentsList = new ArrayList<>();
                                fragmentsField.set(MyHomeFragment.this, fragmentsList);
                            }
                            addFragment(fragmentsList);
                            Log.d(TAG, "addFragment() called before super.initView(), count: " + fragmentsList.size());
                            break;
                        } catch (NoSuchFieldException e) {
                            currentClass = currentClass.getSuperclass();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getting fragments field before super.initView()", e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error calling addTabs/addFragment before super.initView()", e);
                e.printStackTrace();
            }
            
            super.initView(rootView);
            Log.d(TAG, "MyHomeFragment initView completed");
            
            // 尝试调用 startInit() 或刷新 ViewPager
            if (rootView != null) {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Post runnable executed, attempting to refresh ViewPager");
                        try {
                            // 尝试通过反射调用 startInit() 方法
                            try {
                                java.lang.reflect.Method startInitMethod = null;
                                Class<?> currentClass = MyHomeFragment.this.getClass();
                                while (currentClass != null && currentClass != Object.class) {
                                    try {
                                        startInitMethod = currentClass.getDeclaredMethod("startInit");
                                        startInitMethod.setAccessible(true);
                                        startInitMethod.invoke(MyHomeFragment.this);
                                        Log.d(TAG, "Called startInit() method");
                                        break;
                                    } catch (NoSuchMethodException e) {
                                        currentClass = currentClass.getSuperclass();
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "startInit() method not found or failed", e);
                            }
                            
                            // 尝试通过反射获取 ViewPager 并设置可见性
                            try {
                                java.lang.reflect.Field viewPagerField = null;
                                Class<?> currentClass = MyHomeFragment.this.getClass();
                                while (currentClass != null && currentClass != Object.class) {
                                    try {
                                        viewPagerField = currentClass.getDeclaredField("mViewPager");
                                        viewPagerField.setAccessible(true);
                                        android.view.View viewPager = (android.view.View) viewPagerField.get(MyHomeFragment.this);
                                        if (viewPager != null) {
                                            viewPager.setVisibility(android.view.View.VISIBLE);
                                            Log.d(TAG, "Set ViewPager visibility to VISIBLE");
                                        }
                                        break;
                                    } catch (NoSuchFieldException e) {
                                        currentClass = currentClass.getSuperclass();
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Could not find or set ViewPager", e);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in post runnable", e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in initView", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onActivityCreated(android.os.Bundle savedInstanceState) {
        Log.d(TAG, "MyHomeFragment onActivityCreated called");
        try {
            super.onActivityCreated(savedInstanceState);
            Log.d(TAG, "MyHomeFragment onActivityCreated completed");
        } catch (Exception e) {
            Log.e(TAG, "Error in onActivityCreated", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "MyHomeFragment onStart called");
        super.onStart();
        Log.d(TAG, "MyHomeFragment onStart completed");
    }

    private boolean isInitialized = false;

    @Override
    public void onResume() {
        Log.d(TAG, "MyHomeFragment onResume called");
        super.onResume();
        Log.d(TAG, "MyHomeFragment onResume completed");
        
        // 如果还没有初始化，尝试手动触发
        if (!isInitialized && getView() != null) {
            Log.d(TAG, "Attempting to manually initialize tabs and fragments");
            try {
                // 尝试通过反射或其他方式触发初始化
                // 或者直接调用 addTabs 和 addFragment
                // 但需要先检查 BaseHomeFragment 的内部状态
                isInitialized = true;
            } catch (Exception e) {
                Log.e(TAG, "Error in manual initialization", e);
            }
        }
    }

    @Override
    protected boolean isExitFragment() {
        return true;
    }

    @Override
    protected boolean isNeedChangeStatusBarMode() {
        return true;
    }

    @Override
    protected boolean isViewPagerSwipe() {
        return false;
    }

    @Override
    protected boolean isDestroyItem() {
        return false;
    }

    @Override
    protected void addTabs() {
        try {
            Log.d(TAG, "addTabs started");
            String[] tabBars = CommonData.getTabBars();
            
            // 保底逻辑：如果获取失败，使用默认值
            if (tabBars == null || tabBars.length < 3) {
                Log.w(TAG, "TabBars is null or length < 3, using default tabs");
                tabBars = new String[]{"主页", "搜索", "个人"};
            }

            addTab(R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_home_select_24, tabBars[0]);
            addTab(R.drawable.ic_baseline_search_24, R.drawable.ic_baseline_search_select_24, tabBars[1]);
            addTab(R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_person_select_24, tabBars[2]);
            Log.d(TAG, "addTabs completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in addTabs", e);
            // 最后的挣扎：添加最基础的 Tab 避免完全白屏
            try {
                addTab(R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_home_select_24, "Home");
                addTab(R.drawable.ic_baseline_search_24, R.drawable.ic_baseline_search_select_24, "Search");
                addTab(R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_person_select_24, "Me");
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected void addFragment(ArrayList<BaseFragment> fragments) {
        try {
            Log.d(TAG, "addFragment started");
            fragments.add(new ShelfFragment());
            fragments.add(new SearchBaseFragment());
            fragments.add(new PersonFragment());
            Log.d(TAG, "addFragment completed, count: " + fragments.size());
        } catch (Exception e) {
            Log.e(TAG, "Error in addFragment", e);
        }
    }

}
