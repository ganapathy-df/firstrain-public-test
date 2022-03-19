package com.firstrain.frapi.service.filters;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventSelector;
import com.firstrain.mip.object.FR_IEventEntity;

/**
 * @author Akanksha
 * 
 */

public interface Selectors {

	class WebVolumeSelector implements EventSelector {
		private final double threshold;
		private final EventTypeEnum ete;
		private final boolean upperLimitThreshold;

		public WebVolumeSelector(double threshold) {
			this(threshold, true);
		}

		public WebVolumeSelector(double threshold, boolean upperLimitThreshold) {
			this(threshold, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_COMPANY, upperLimitThreshold);
		}

		public WebVolumeSelector(double threshold, EventTypeEnum ete) {
			this(threshold, ete, true);
		}

		public WebVolumeSelector(double threshold, EventTypeEnum ete, boolean upperLimitThreshold) {
			this.threshold = threshold;
			this.ete = ete;
			this.upperLimitThreshold = upperLimitThreshold;
		}

		@Override
		public boolean isSelected(IEvents e) {
			if (e.getEventType() == this.ete) {
				// TODO: For now using score instead of relevance.
				if (this.upperLimitThreshold && e.getScore() < this.threshold) {
					return true;
				} else if (!this.upperLimitThreshold && e.getScore() >= this.threshold) {
					return true;
				}
			}
			return false;
		}
	}
	class StockPriceChangeSelector implements EventSelector {
		private final double threshold;
		private final boolean upperLimitThreshold;

		public StockPriceChangeSelector(double threshold) {
			this(threshold, true);
		}

		/**
		 * Constructs a {@link StockPriceChangeSelector} to qualify stock price events only that meets the criteria specified.
		 * 
		 * @param threshold The threshold value for stock price change in percent.
		 * @param upperLimitThreshold If true(default) indicates that this threshold value is the upper boundary for this selector,
		 *        otherwise its the lower boundary to meet.
		 */
		public StockPriceChangeSelector(double threshold, boolean upperLimitThreshold) {
			this.threshold = threshold;
			this.upperLimitThreshold = upperLimitThreshold;
		}

		@Override
		public boolean isSelected(IEvents e) {
			if (e.getEventType() == IEvents.EventTypeEnum.TYPE_STOCK_PRICE_CHANGE) {
				Object changeObj = e.getProperties().get("percentChange");
				if (changeObj == null) {
					return true;
				}

				double pctChange = 0;
				if (changeObj instanceof Number) {
					pctChange = ((Number) changeObj).doubleValue();
				} else if (changeObj instanceof String) {
					String changeStr = ((String) changeObj).trim();
					try {
						pctChange = Double.parseDouble(changeStr);
					} catch (NumberFormatException ex) {
						// Drop the event.
						return true;
					}
				} else {
					return true;
				}
				pctChange = Math.abs(pctChange);
				if (this.upperLimitThreshold && pctChange < this.threshold) {
					return true;
				} else if (!this.upperLimitThreshold && pctChange >= this.threshold) {
					return true;
				}
			}
			return false;
		}
	}
	class TypeSelector implements EventSelector {
		private final IEvents.EventTypeEnum[] types;

		public TypeSelector(IEvents.EventTypeEnum... types) {
			this.types = types;
		}

		@Override
		public boolean isSelected(IEvents e) {
			for (EventTypeEnum t : types) {
				if (e.getEventType() == t) {
					return true;
				}
			}
			return false;
		}
	}
	class GroupSelector implements EventSelector {
		private final IEvents.EventGroupEnum group;

		public GroupSelector(IEvents.EventGroupEnum group) {
			this.group = group;
		}

		@Override
		public boolean isSelected(IEvents e) {
			if (e.getEventGroup() == group) {
				return true;
			}
			return false;
		}
	}
	class InternalTurnoverSelector implements EventSelector {
		@Override
		public boolean isSelected(IEvents e) {
			// Management change internal moves
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE) {
				return (e.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) == FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE;
			}
			return false;
		}
	}
	class HireSelector implements EventSelector {
		@Override
		public boolean isSelected(IEvents e) {
			// Management change hire
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE) {
				return (e.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) == FR_IEventEntity.FLAG_BITMAP_MASK_HIRE;
			}
			return false;
		}
	}
	class DepartureSelector implements EventSelector {
		@Override
		public boolean isSelected(IEvents e) {
			// Management change departure
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE) {
				return (e.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) == FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE;
			}
			return false;
		}
	}
	class LowRankedInternalMgmtTurnoverSelector implements EventSelector {
		@Override
		public boolean isSelected(IEvents e) {
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE
					&& (e.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) == FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) {
				return !e.getEventType().isHighRankedInternalMgmtTurnover();
			}
			return false;
		}
	}
	class NonexecTurnoverSelector implements EventSelector {

		@Override
		public boolean isSelected(IEvents e) {
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE && e.getEventType().isNonExecTurnover()) {
				return true;
			}
			return false;
		}
	}
}
