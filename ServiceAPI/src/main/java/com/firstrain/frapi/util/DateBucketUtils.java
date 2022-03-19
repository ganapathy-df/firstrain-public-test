package com.firstrain.frapi.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;

/**
 * @author Gkhanchi
 * 
 */

@Service
public class DateBucketUtils {

	private final int MAX_BUCKET_SIZE = 18;

	public static class BucketSpec {
		public DateBucketingMode mode = DateBucketingMode.AUTO;
		public String dateFieldOrMethodName;
		public int bucketSizeThreshold = 0;
		/**
		 * set true if need day by bucketing
		 */
		public boolean dateBucket;
		public Date currentDate; // for testing purpose - to set any date to starts with

	}

	/**
	 * @param list must be sorted by date on which one need grouping
	 * @param dateFieldOrMethodName name of field or method to access date for grouping purpose, if object having method and field name same
	 *        to provided name method will get priority
	 * @return <code>Map&lt;String, List&lt;T>></code> date v/s list of objects, map will be sorted by date
	 * @throws Exception
	 */

	public <T> Map<String, List<T>> getListGroupByDate(List<T> list, BucketSpec spec) throws Exception {
		if (list == null || list.isEmpty()) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		if (spec.currentDate != null) {
			cal.setTime(spec.currentDate);
		}
		DateBucketingMode mode = spec.mode;
		if (mode == DateBucketingMode.AUTO) {
			spec.mode = DateBucketingMode.SMART;
		}
		List<LabelVsDate> buckets = getDateBuckets(cal, spec);
		SimpleDateFormat lblFormat =
				(spec.mode == DateBucketingMode.SMART ? new SimpleDateFormat("MMM-yyyy") : new SimpleDateFormat("dd-MMM-yyyy"));
		Map<String, List<T>> result = new LinkedHashMap<String, List<T>>();
		Method method = null;
		Field field = null;
		int pointer = 0;
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if (method == null && field == null) {
				try {
					method = t.getClass().getMethod(spec.dateFieldOrMethodName);
					method.setAccessible(true);
				} catch (NoSuchMethodException e) {
					field = t.getClass().getDeclaredField(spec.dateFieldOrMethodName);
					field.setAccessible(true);
				}
			}
			Date date = (Date) (method != null ? method.invoke(t) : field.get(t));
			if (pointer < buckets.size()) {
				boolean added = false;
				for (; pointer < buckets.size(); pointer++) {
					LabelVsDate labelVsDate = buckets.get(pointer);
					if (!date.before(labelVsDate.date)) { // if date is equal or after
						List<T> tmpList = result.get(labelVsDate.label);
						if (tmpList == null) {
							tmpList = new ArrayList<T>();
							result.put(labelVsDate.label, tmpList);
						}
						tmpList.add(t);
						added = true;
						if (mode == DateBucketingMode.AUTO && tmpList.size() == MAX_BUCKET_SIZE) { // switch to DATE mode
							spec.mode = DateBucketingMode.DATE;
							return getListGroupByDate(list, spec);
						}
						break;
					}
				}
				if (!added) {
					i--;
				}
			} else { // do monthly/date bucketing
				String label = lblFormat.format(date);
				List<T> tmpList = result.get(label);
				if (tmpList == null) {
					tmpList = new ArrayList<T>();
					result.put(label, tmpList);
				}
				tmpList.add(t);
				if (mode == DateBucketingMode.AUTO && tmpList.size() == MAX_BUCKET_SIZE) { // switch to DATE mode
					spec.mode = DateBucketingMode.DATE;
					return getListGroupByDate(list, spec);
				}
			}
		}
		if (spec.mode == DateBucketingMode.SMART) {
			ensureBucketSize(result, spec, buckets);
		}
		return result;
	}

	private <T> void ensureBucketSize(Map<String, List<T>> result, BucketSpec spec, List<LabelVsDate> buckets) {
		if (buckets == null) {
			return;
		}
		for (LabelVsDate bucket : buckets) {
			if (bucket.bucketToMerge == null) {
				return;
			}
			List<T> list = result.get(bucket.label);
			if (list != null && list.size() < spec.bucketSizeThreshold) {
				List<T> list1 = result.get(bucket.bucketToMerge);
				if (list1 != null && !list1.isEmpty()) {
					list1.addAll(0, list);
					result.remove(bucket.label);
				}
			}
		}
	}

	/**
	 * a. In last 24 hours/ Today b. In this week c. Last week/ Previous week/ Week of [Date] d. Two weeks ago/ Week of [Date] e. Three
	 * weeks ago/ Week of [Date] f. Earlier this month/ Four weeks ago/ Week of [Date] g. Last Month h. Older/Month Name
	 */

	protected class LabelVsDate {
		public String label;
		public Date date;
		public String bucketToMerge;

		public LabelVsDate(String label, Date date) {
			this.label = label;
			this.date = date;
		}

		@Override
		public String toString() {
			return label + " == " + date;
		}
	}

	protected List<LabelVsDate> getDateBuckets(Calendar cal, BucketSpec spec) {

		int currentWeek = cal.get(Calendar.WEEK_OF_MONTH);
		List<LabelVsDate> list = new ArrayList<LabelVsDate>();
		if (spec.dateBucket) {
			// trim date
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			list.add(new LabelVsDate("Today", cal.getTime())); // today
		} else {
			cal.add(Calendar.DATE, -1);
			cal.add(Calendar.SECOND, 1);
			list.add(new LabelVsDate("Last 24 hours", cal.getTime())); // Last 24 hours

			// trim date
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		if (spec.mode == DateBucketingMode.DATE) { // it has only 24 hrs/today bucket
			return list;
		}
		int currentWeek1 = cal.get(Calendar.WEEK_OF_MONTH);
		if (currentWeek1 <= currentWeek) {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			SimpleDateFormat ddMMMyyyy = new SimpleDateFormat("dd-MMM-yyyy");
			do {
				Date date = cal.getTime();
				String lbl = "Week of " + ddMMMyyyy.format(date);
				if (currentWeek1 == currentWeek) { // if its same week, 24/today bucket can be merged to it
					list.get(0).bucketToMerge = lbl;
				}
				list.add(new LabelVsDate(lbl, date));
				cal.add(Calendar.WEEK_OF_MONTH, -1);
			} while (--currentWeek1 > 0);
		}
		return list;
	}
}

