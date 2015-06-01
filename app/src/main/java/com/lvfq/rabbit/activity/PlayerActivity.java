package com.lvfq.rabbit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lvfq.rabbit.R;
import com.youku.player.base.YoukuBasePlayerActivity;
import com.youku.player.base.YoukuPlayer;
import com.youku.player.base.YoukuPlayerView;

/**
 * 播放器播放界面，需要继承自YoukuBasePlayerActivity基类
 *
 */
public class PlayerActivity extends YoukuBasePlayerActivity {
	//播放器控件
	private YoukuPlayerView mYoukuPlayerView;
	
	//需要播放的视频id
	private String vid;


	//需要播放的本地视频的id
	private String local_vid;

	//标示是否播放的本地视频
	private boolean isFromLocal = false;
	
	private String id = "";
	
	//YoukuPlayer实例，进行视频播放控制
	private YoukuPlayer youkuPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.second);
		
		//通过上个页面传递过来的Intent获取播放参数
		getIntentData(getIntent());

		if(TextUtils.isEmpty(id)){
			vid = "XODYwNTAzODIw";	//默认视频
		}else{
			vid = id;
		}

		//播放器控件
		mYoukuPlayerView = (YoukuPlayerView) this.findViewById(R.id.full_holder);

		//初始化播放器相关数据
		mYoukuPlayerView.initialize(this);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		//通过Intent获取播放需要的相关参数
		getIntentData(intent);

		//进行播放
		goPlay();
	}

	
	/**
	 * 获取上个页面传递过来的数据
	 */
	private void getIntentData(Intent intent){
		
		if(intent != null){
			//判断是不是本地视频
			isFromLocal = intent.getBooleanExtra("isFromLocal", false);
			
			if(isFromLocal){	//播放本地视频
				local_vid = intent.getStringExtra("video_id");				
			}else{				//在线播放
				id = intent.getStringExtra("vid");
			}
		}
		
	}

	@Override
	public void setPadHorizontalLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationSuccess(YoukuPlayer player) {
		// TODO Auto-generated method stub
		//初始化成功后需要添加该行代码
		addPlugins();

		//实例化YoukuPlayer实例
		youkuPlayer = player;

		//进行播放
		goPlay();
	}
	
	private void goPlay(){
		if(isFromLocal){	//播放本地视频
			youkuPlayer.playLocalVideo(local_vid);			
		}else{				//播放在线视频
			youkuPlayer.playVideo(vid);
		}

		//XNzQ3NjcyNDc2
		//XNzQ3ODU5OTgw
		//XNzUyMzkxMjE2
		//XNzU5MjMxMjcy  加密视频
		//XNzYxNzQ1MDAw 万万没想到
		//XNzgyODExNDY4	魔女范冰冰扑倒黄晓明
		//XNDcwNjUxNzcy 姐姐立正向前走
		//XNDY4MzM2MDE2 向着炮火前进
		//XODA2OTkwMDU2 卧底韦恩突出现 劫持案愈发棘手
		//XODUwODM2NTI0	会员视频
		//XODQwMTY4NDg0 一个人的武林
	}

	@Override
	public void onFullscreenListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSmallscreenListener() {
		// TODO Auto-generated method stub
		
	}


}
