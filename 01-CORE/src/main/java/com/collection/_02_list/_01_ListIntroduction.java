package com.collection._02_list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class _01_ListIntroduction {
    public static void main(String[] args) {
        // listIsAnOrderCollection();
        // indexOfList();
        // modifyList();
        // listContainDuplicateElements();
        // utilityMethod();
        iterateCollection();
    }

    /**
     * List là một danh sách các phần tử có thể lưu lại thứ tự thêm vào
     * 
     * - NX: Bất kể implement của nó có là gì. Các class implmeent từ List đều phải
     * tuân thủ duy trì thứ tự thêm các phần tử
     */
    public static void listIsAnOrderCollection() {
        List<Integer> arrayList = new ArrayList<>();

        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);

        System.out.println(arrayList); // [1, 2, 3] - Các phần tử được lưu lại đúng như thứ tự nó thêm vào

        List<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);
        stack.add(3);

        System.out.println(stack); // [1, 2, 3]

        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        System.out.println(linkedList); // [1, 2, 3]

    }

    /**
     * List hỗ trợ truy cập bằng index
     * 
     * NX:
     * - List cung cấp hỗ trợ truy cập bằng index
     * - Và khi truy cập vào chỉ mục không tồn tại thì sẽ throw ra ex
     * IndexOutOfBoundsException
     */
    public static void indexOfList() {
        List<Integer> arrayList = new ArrayList<>(List.of(1, 2, 3));
        System.out.println(arrayList.get(0));// 1
        System.out.println(arrayList.get(2)); // 3

        try {
            arrayList.get(3);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Truy cập ngoài giới hạn thì throw IndexOutOfBoundsException");
        }

        List<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);
        stack.add(3);
        System.out.println(stack.get(0));// 1
        System.out.println(stack.get(2));// 3
    }

    /**
     * List có thể thêm đọc sửa xóa ở bất kì vị trí nào
     * 
     * NX:
     * - List không bị giới hạn số lượng phần tử như array nguyên thủy
     * - Cung cấp các method chèn vào đầu. vào giữa, xóa tự động cập nhật lại index
     * các phần tử khác
     */
    public static void modifyList() {
        // Khởi tạo List không khai báo kích thước
        List<Integer> stack = new Stack<>();

        // Chèn các phần tử vòa cuối
        stack.add(1);
        stack.add(2);
        System.out.println(stack); // [1, 2]
        System.out.println(stack.size()); // 2 -> có 2 phần tử

        // Chèn phần tử vào giữa
        stack.add(1, 3);
        System.out.println(stack); // [1, 3, 2] - Phần tử được thêm vào. Ko cần thao tác dịch chuyển

        // Xóa phần tử ở đầu
        System.out.println(stack.size()); // 3
        stack.remove(0);
        System.out.println(stack); // [3, 2] - phần tử đầu đã được xóa đi.
        System.out.println(stack.size()); // 2 - arr tự giảm kích thước

    }

    /**
     * List chấp nhận các phần tử trùng lặp
     */
    public static void listContainDuplicateElements() {
        List<Integer> arrayList = new ArrayList<>(List.of(1, 2, 3, 1));
        System.out.println(arrayList); // [1, 2, 3, 1] - Có 2 số 1
    }

    /**
     * MỘt số method của List cung cấp sẵn cho việc làm với dữ liệu
     * 
     * - NX: List extends Collection
     * 
     * - Nên List có chứa các method hữu ích từ Collection. Và bản thân nó nữa
     */
    public static void utilityMethod() {
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));

        // IndexOf: Kiểm tra vị trí của phần tử
        System.out.println(nums.indexOf(3)); // 2 -> index của phần tử
        System.out.println(nums.indexOf(-100)); // -1 -> -1 khi không tim thấy

        // Kiểm tra sự tồn tại
        System.out.println(nums.contains(1));// true
        System.out.println(nums.contains(-100)); // false

        // Lấy kích thước của mảng
        System.out.println(nums.size()); // 6

        // Sắp xếp mảng
        nums.sort(Comparator.reverseOrder());
        System.out.println(nums); // [6, 5, 4, 3, 2, 1]

    }

    /**
     * Các cách duyệt qua List
     * 
     * - NX
     * 
     * - List extends Collection. Nên ta có thể duyệt như một Collection
     * 
     * - List extends Iterable
     */
    public static void iterateCollection() {
        List<Integer> nums = List.of(1, 2, 3, 4, 5);

        // Duyệt bằng vòng lặp

        for (int i = 0; i < nums.size(); i++) {
            System.out.print(nums.get(i) + " ");
        }
        System.out.println();
        // 1 2 3 4 5

        // Duyệt bằng forEach
        for (Integer integer : nums) {
            System.out.print(integer + " ");
        }
        System.out.println();

        // 1 2 3 4 5

        // Duyệt bằng Iterable
        Iterator<Integer> ite = nums.iterator();
        while (ite.hasNext()) {
            System.out.print(ite.next() + " ");
        }
        System.out.println();
        // 1 2 3 4 5

        // Duyệt bằng method forEach của Collection
        nums.forEach(i -> System.out.print(i + " "));
        System.out.println();
        // 1 2 3 4 5
    }
}
