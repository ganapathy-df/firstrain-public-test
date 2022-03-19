package com.firstrain.frapi.util;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Tags.SearchOrderType;
import com.firstrain.db.obj.TagsInfo;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MonitorOrderingUtilsTest {

  @Test
  public void givenParametersFalseDateOrderTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.DATE);
    List<TagsInfo> tagInfoList = buildTimeStampTagsInfo(false);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getInsertTime(), tagInfoList.get(1).tag.getInsertTime());
  }

  @Test
  public void givenParametersTrueDateOrderTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.DATE);
    List<TagsInfo> tagInfoList = buildTimeStampTagsInfo(true);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getInsertTime(), tagInfoList.get(1).tag.getInsertTime());
  }

  @Test
  public void givenParametersFalseCustomOrderTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.CUSTOM);
    List<TagsInfo> tagInfoList = buildOrderTagsInfo(false);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getOrder(), tagInfoList.get(1).tag.getOrder());
  }

  @Test
  public void givenParametersTrueCustomOrderTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.CUSTOM);
    List<TagsInfo> tagInfoList = buildOrderTagsInfo(true);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getOrder(), tagInfoList.get(1).tag.getOrder());
  }

  @Test
  public void givenParametersFalseNameTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.NAME);
    List<TagsInfo> tagInfoList = buildNameTagsInfo(false);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getOrder(), tagInfoList.get(1).tag.getOrder());
  }

  @Test
  public void givenParametersTrueNameTypeWhenSortGroupMonitorByOrderTypeThenResult() {
    //Arrange
    Groups group = new Groups();
    group.setMonitorOrderType(SearchOrderType.NAME);
    List<TagsInfo> tagInfoList = buildNameTagsInfo(true);

    //Act
    MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);

    //Assert
    assertEquals(tagInfoList.get(0).tag.getOrder(), tagInfoList.get(1).tag.getOrder());
  }

  private static List<TagsInfo> buildTimeStampTagsInfo(boolean flag) {
    long timeStamp = System.currentTimeMillis();
    TagsInfo tagsInfo1 = new TagsInfo();
    tagsInfo1.favorite = flag;
    Tags tags1 = mock(Tags.class);
    when(tags1.getInsertTime()).thenReturn(new Timestamp(timeStamp));
    tagsInfo1.activityTime = new Timestamp(timeStamp);
    tagsInfo1.tag = tags1;
    TagsInfo tagsInfo2 = new TagsInfo();
    tagsInfo2.favorite = flag;
    Tags tags2 = mock(Tags.class);
    when(tags2.getInsertTime()).thenReturn(new Timestamp(timeStamp));
    tagsInfo2.activityTime = new Timestamp(timeStamp);
    tagsInfo2.tag = tags2;
    List<TagsInfo> tagInfoList = new ArrayList<TagsInfo>();
    tagInfoList.add(tagsInfo1);
    tagInfoList.add(tagsInfo2);
    return tagInfoList;
  }

  private static List<TagsInfo> buildOrderTagsInfo(boolean flag) {
    long timeStamp = System.currentTimeMillis();
    int order = 9;
    TagsInfo tagsInfo1 = new TagsInfo();
    tagsInfo1.favorite = flag;
    Tags tags1 = mock(Tags.class);
    when(tags1.getOrder()).thenReturn(order);
    tagsInfo1.tag = tags1;
    tagsInfo1.activityTime = new Timestamp(timeStamp);
    TagsInfo tagsInfo2 = new TagsInfo();
    tagsInfo2.favorite = flag;
    Tags tags2 = mock(Tags.class);
    when(tags2.getOrder()).thenReturn(order);
    tagsInfo2.tag = tags2;
    tagsInfo2.activityTime = new Timestamp(timeStamp);
    List<TagsInfo> tagInfoList = new ArrayList<TagsInfo>();
    tagInfoList.add(tagsInfo1);
    tagInfoList.add(tagsInfo2);
    return tagInfoList;
  }

  private static List<TagsInfo> buildNameTagsInfo(boolean flag) {
    long timeStamp = System.currentTimeMillis();
    String name = "Name";
    TagsInfo tagsInfo1 = new TagsInfo();
    tagsInfo1.favorite = flag;
    Tags tags1 = mock(Tags.class);
    when(tags1.getTagName()).thenReturn(name);
    tagsInfo1.tag = tags1;
    tagsInfo1.activityTime = new Timestamp(timeStamp);
    tagsInfo1.favoriteUserItemId = "567";
    TagsInfo tagsInfo2 = new TagsInfo();
    tagsInfo2.favorite = flag;
    Tags tags2 = mock(Tags.class);
    when(tags2.getTagName()).thenReturn(name);
    tagsInfo2.tag = tags2;
    tagsInfo2.activityTime = new Timestamp(timeStamp);
    tagsInfo2.favoriteUserItemId = "567";
    List<TagsInfo> tagInfoList = new ArrayList<TagsInfo>();
    tagInfoList.add(tagsInfo1);
    tagInfoList.add(tagsInfo2);
    return tagInfoList;
  }
}
