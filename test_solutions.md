# OJ系统测试代码示例

## 1. Hello World (题目ID: 3)

### 正确解法：
```python
print("Hello, World!")
```

### 错误解法（用于测试失败情况）：
```python
print("Hello World")  # 缺少逗号和感叹号
```

## 2. 计算两数之积 (题目ID: 4)

### 正确解法：
```python
a = int(input())
b = int(input())
print(a * b)
```

### 另一种正确解法：
```python
x = int(input())
y = int(input())
result = x * y
print(result)
```

### 错误解法（用于测试失败情况）：
```python
a = int(input())
b = int(input())
print(a + b)  # 错误：应该是乘法，不是加法
```

## 3. 判断奇偶数 (题目ID: 5)

### 正确解法：
```python
n = int(input())
if n % 2 == 0:
    print("even")
else:
    print("odd")
```

### 另一种正确解法：
```python
num = int(input())
print("even" if num % 2 == 0 else "odd")
```

### 错误解法（用于测试失败情况）：
```python
n = int(input())
if n % 2 == 0:
    print("Even")  # 错误：大小写不匹配
else:
    print("Odd")   # 错误：大小写不匹配
```

## 4. 数组求和 (题目ID: 6)

### 正确解法：
```python
n = int(input())
numbers = list(map(int, input().split()))
print(sum(numbers))
```

### 另一种正确解法：
```python
n = int(input())
arr = input().split()
total = 0
for i in range(n):
    total += int(arr[i])
print(total)
```

### 错误解法（用于测试失败情况）：
```python
n = int(input())
numbers = list(map(int, input().split()))
print(sum(numbers) / n)  # 错误：计算的是平均值，不是总和
```

## 5. 两数之和 (题目ID: 2) - 较复杂

### 正确解法：
```python
n = int(input())
nums = list(map(int, input().split()))
target = int(input())

for i in range(n):
    for j in range(i + 1, n):
        if nums[i] + nums[j] == target:
            print(i, j)
            break
    else:
        continue
    break
```

### 优化解法（哈希表）：
```python
n = int(input())
nums = list(map(int, input().split()))
target = int(input())

num_map = {}
for i, num in enumerate(nums):
    complement = target - num
    if complement in num_map:
        print(num_map[complement], i)
        break
    num_map[num] = i
```

