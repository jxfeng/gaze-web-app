package gaze.video.handler.dydb;

import gaze.video.entity.CameraShard;
import gaze.video.entity.dynamodb.DynamoDBCameraShard;

import java.util.ArrayList;
import java.util.List;


public class DyCameraShardEntityBuilder {

	public static CameraShard build(DynamoDBCameraShard dyShard) {
		CameraShard shard = new CameraShard();
		shard.setUserId(dyShard.getUserId());
		shard.setCameraId(dyShard.getCameraKey());
		shard.setShardId(dyShard.getShardId());
		shard.setShardBeginTimestamp(new DyImageHandler().getStartTimestamp(dyShard.getShardId()));
		shard.setShardEndTimestamp(new DyImageHandler().getEndTimestamp(dyShard.getShardId()));
		return shard;
	}
	
	public static List<CameraShard> buildShardList(List<DynamoDBCameraShard> dyShardList) {
		List<CameraShard> shardList = new ArrayList<CameraShard>();
		if(dyShardList != null && dyShardList.size() > 0) {
			for(DynamoDBCameraShard dyShard : dyShardList) {
				shardList.add(build(dyShard));
			}
		}
		return shardList;
	}
	
}
