package com.example.jake.caastme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

import static android.R.string.no;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView textViewPos;
        TextView textViewData;
        TextView deleteTextView;
        TextView topTextView;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textViewPos = (TextView) itemView.findViewById(R.id.position);
            textViewData = (TextView) itemView.findViewById(R.id.text_data);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewData.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewData.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Context mContext;
    private ArrayList<String> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);



    //Adapter的构造函数。初始化一些数据
    public RecyclerViewAdapter(Context context, ArrayList<String> objects) {
        this.mContext = context;
        this.mDataset = objects;
    }



    //构造ViewHolder ,就是加载recyclerview中的每一项
    /**
     * What you see is an expected behaviour. RecylerView won't create as many view as adapter's
     * getItemCount() returns. It create view as many as it's necessary for rendering a view.

     For instance, if your adapter contains 100 elements, but only 5 are displayed at at time,
     system will create 5 views (it may create 1 or 2 more view as "buffer") and reuse these views when user scrolls.
    *
    *
    * */
    //只会被调用够一次手机屏幕展示的 次数，因为你每次展示的就那几个。没必要创建getItemCount数目的ViewHolder
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("loadssss","create");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }


    //对recyclerview每一项进行操作，设置蚊子显示，监听什么的
    //@viewHolder 当前的viewHolder,也有可能是新的，也有可能是缓存的。因为recyclerView有利于资源的回收


    /*
       至于为什么要在onBindViewHolder中绑定监听，设置文字

       本来我是想在onCreateViewHolder中 每创建一个SimpleViewHolder,就绑定一次监听，因为onBindViewHolder
       中调用的就是 create中创建的几个实例，在bind中不是重复在同一个对象上重复绑定了吗？

       其实不是这样的，还是那几个viewHolder实例没错，但是他要表示不同位置的每项
       比如说你刚开始的时候创建了几个ViewHolder的实例。那是recyclerVIew中开头几个展示的实例。这几个实例
       还有去表示你滑动到最后几个的每一项，你在oncreate中绑定的话，就永远是那前几个有绑定监听，后面就没有了！
     */

    private SwipeLayout tempLayout;//记住上次打开的那个swipeLayout
    private int tempLayoutIndex;//记住上次打开的那个swipeLayout position

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        Log.i("loadssss","bind#####"+position);

        String item = mDataset.get(position);

        //因为每一项中都是一个swipelayout,所以每一项都要设置showMode.。不然是他的默认模式
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
       // viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,viewHolder.swipeLayout.getChildAt(0));
        viewHolder.textViewPos.setText((position + 1) + ".");
        viewHolder.textViewData.setText(item);


        //滑动监听
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
               // YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));

                //有的时候，滑的太快不算点击事件，导致无法关闭，这里增加滑动也要关闭
                if(tempLayout!=null && position!=tempLayoutIndex){
                    tempLayout.close();
                    Log.i("sdfs","open关闭："+position);
                }

                tempLayout = layout;
                tempLayoutIndex = position;
            }



        });

        //双击监听
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
               //viewHolder.swipeLayout.postDelayed()
                Log.i("obj_address",layout.toString());
                Log.i("obj_address",viewHolder.swipeLayout.toString());


                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        //单击监听
        viewHolder.swipeLayout.setOnClickListener(new SwipeLayout.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(tempLayout!=null){
                    tempLayout.close();
                    tempLayout = null;
                    Log.i("sdfs","关闭："+position);


                }
            /*viewHolder.swipeLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                  // Log.i("sdfs","nativee"+mItemManger.getOpenItems().size());
                }
            }, 50);*/
            }
        });

        //单击关闭 swipeLayout 这个只能关闭自己的。我想不管点击哪个item都能关闭
       //viewHolder.swipeLayout.setClickToClose(true);


        //绑定swipeLayout里面的button
        /*viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mItemManger:父类中的。用户移除当前的swipeLayout
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);

                //这个比较重要;要通知recyclerView，哪一项被移除了，他好去设置新的下标 -1
                 * 仅调用notifyItemRemoved的话,删除会出很多问题,比如:点击删除position = 1的Item,
                 * 实际删除的是下一个,所以我们需要这么做,加上notifyItemRangeChanged这个方法,更新一下列表:
                 *
                 * 先remove,再notifyItemRemoved， 最后再notifyItemRangeChanged
                 remove：把数据从list中remove掉，
                 notifyItemRemoved：显示动画效果
                 notifyItemRangeChanged：对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder


                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });*/

       /* viewHolder.swipeLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

               Log.i("scroll_ljl",view.toString()+"**"+i+"**"+i1+"**"+i2+"**"+i3);

            }
        });*/


    }


    //告知adapter有多少项
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    //这个是daimaijia大神自定义的。表示绑定每一项中的swipeLayout
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}



