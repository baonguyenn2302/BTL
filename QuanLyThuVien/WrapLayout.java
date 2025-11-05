package QuanLyThuVien;

import java.awt.*;
import javax.swing.*;

/**
 * Một FlowLayout tùy chỉnh, tính toán chính xác kích thước
 * (chiều cao) của nó dựa trên chiều rộng của container cha,
 * cho phép tự động ngắt dòng (wrap).
 * (Phiên bản đã sửa lỗi layoutSize)
 */
public class WrapLayout extends FlowLayout {
    
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * HÀM ĐÃ SỬA: Lấy chiều rộng từ JViewport (nếu có)
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            
            int targetWidth;
            
            // ==================================================
            // <<< BẮT ĐẦU SỬA LỖI >>>
            //
            // Lấy container cha. Nếu đó là JViewport (của JScrollPane),
            // chúng ta lấy chiều rộng của JViewport (đây là chiều rộng
            // thực tế có thể hiển thị).
            // Nếu không, chúng ta lấy chiều rộng của chính panel này.
            //
            Container container = target.getParent();
            if (container instanceof JViewport) {
                // Lấy chiều rộng của Viewport và trừ đi 1 chút
                // để đảm bảo nó luôn wrap (không bao giờ
                // kích hoạt thanh cuộn ngang)
                targetWidth = container.getSize().width - 1; 
            } else {
                targetWidth = target.getSize().width;
            }
            // <<< KẾT THÚC SỬA LỖI >>>
            // ==================================================


            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalGaps = hgap;
            // Chiều rộng tối đa cho phép trên một hàng
            int maxWidth = targetWidth - (insets.left + insets.right + horizontalGaps);

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;
            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth > 0 && (rowWidth + d.width > maxWidth)) {
                        // Bắt đầu hàng mới
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = d.width;
                        rowHeight = d.height;
                    } else {
                        // Thêm vào hàng hiện tại
                        if (rowWidth > 0) {
                            rowWidth += hgap;
                        }
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
            }
            // Thêm hàng cuối cùng
            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight; // Không thêm vgap cho hàng cuối

            // Thêm insets
            dim.width += insets.left + insets.right + hgap * 2;
            dim.height += insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }
}