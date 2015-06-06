package com.youku.player.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youku.player.ApiManager;
import com.youku.player.VideoQuality;
import com.youku.player.ui.R;

public class CustomDialog extends Dialog {
	private static int default_width = 210; // 默认宽度
	private static int default_height = 60;// 默认高度
	//清晰度相关按钮
    private TextView btn_standard,btn_hight,btn_super,btn_1080;
    private Context context;

	public CustomDialog(Context context, int layout, int style) {
		this(context, default_width, default_height, layout, style);
	}

	public CustomDialog(Context context, int width, int height, int layout,
			int style) {
		super(context, style);
		// set content
		setContentView(layout);
		this.context=context;
		// set window params
		iniView();
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// set width,height by density and gravity
		float density = getDensity(context);
		params.width = (int) (width * density);
		params.height = (int) (height * density);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}
	private void iniView(){
		btn_standard = (TextView)this.findViewById(R.id.biaoqing);
		btn_hight = (TextView)this.findViewById(R.id.gaoqing);
		btn_super = (TextView)this.findViewById(R.id.chaoqing);
		btn_1080 = (TextView)this.findViewById(R.id.most);
		
		btn_standard.setOnClickListener(listener);
		btn_hight.setOnClickListener(listener);
		btn_super.setOnClickListener(listener);
		btn_1080.setOnClickListener(listener);
	}
	
	public View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if(view.getId()==R.id.biaoqing){				//切换标清
				change(VideoQuality.STANDARD);
			}else if(view.getId()==R.id.gaoqing){			//切换高清
				change(VideoQuality.HIGHT);
			}else if(view.getId()==R.id.chaoqing){			//切换高清
				change(VideoQuality.SUPER);
			}else if(view.getId()==R.id.most){			//切换高清
				change(VideoQuality.P1080);
			}
			CustomDialog.this.dismiss();
		}
	};
	
	/**
	 * 更改视频的清晰度
	 * @param quality
	 * 				VideoQuality有四种枚举值：{STANDARD,HIGHT,SUPER,P1080}，分别对应：标清，高清，超清，1080P
	 */
	
	private void change(VideoQuality quality){
		try{
			//通过ApiManager实例更改清晰度设置，返回值（1):成功；（0): 不支持此清晰度
			//接口详细信息可以参数使用文档
			int result = ApiManager.getInstance().changeVideoQuality(quality,(Activity)context);
			if(result == 0) Toast.makeText((Activity)context, "不支持此清晰度", 2000).show();
		}catch(Exception e){
			Toast.makeText((Activity)context, e.getMessage(), 2000).show();
		}
	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}
}