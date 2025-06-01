# 改进OJ系统解题示例

## 解题示例

### 1. 多行数字求和 (题目ID: 7)

**输入格式**（数据库存储）：
```
3\n1 2 3\n4 5\n6 7 8 9
```

**实际输入效果**：
```
3
1 2 3
4 5
6 7 8 9
```

**Python解法**：
```python
n = int(input())
for i in range(n):
    numbers = list(map(int, input().split()))
    print(sum(numbers))
```

**期望输出**：
```
6
9
30
```

### 2. 格式化输出 (题目ID: 7)

**输入**：`Alice Bob Charlie`

**Python解法**：
```python
names = input().split()
for i, name in enumerate(names, 1):
    print(f"Name {i}: {name}")
```

**期望输出**（数据库存储）：
```
Name 1: Alice\nName 2: Bob\nName 3: Charlie
```

### 3. 圆的面积 (题目ID: 8)

**Python解法**：
```python
import math

radius = float(input())
area = math.pi * radius * radius
print(f"{area:.3f}")
```

### 4. 矩阵转置 (题目ID: 9)

**输入格式**（数据库存储）：
```
2 3\n1 2 3\n4 5 6
```

**Python解法**：
```python
m, n = map(int, input().split())
matrix = []
for i in range(m):
    row = list(map(int, input().split()))
    matrix.append(row)

# 转置
for j in range(n):
    for i in range(m):
        print(matrix[i][j], end=' ' if i < m-1 else '\n')
```

### 5. 空行处理 (题目ID: 10)

**Python解法**：
```python
try:
    line = input()
    if line:
        print(line)
    else:
        print("")
except:
    print("")
```

## 系统改进对比

### 原系统 vs 改进系统

| 特性 | 原系统 | 改进系统 |
|------|--------|----------|
| 输入处理 | 仅空格分隔 | 空格分隔 + 多行输入 |
| 输出比较 | 按空格分割比较 | 智能多行比较 |
| 换行支持 | ❌ | ✅ 支持`\n`转义 |
| 容错性 | 低 | 高（忽略行尾空格） |
| 复杂度 | 简单 | 适中 |

### 数据库存储示例

```sql
-- 多行输入示例
INSERT INTO test_case (question_id, input, output) VALUES
(6, '3\n1 2 3\n4 5\n6 7 8 9', '6\n9\n30');

-- 多行输出示例  
INSERT INTO test_case (question_id, input, output) VALUES
(7, 'Alice Bob Charlie', 'Name 1: Alice\nName 2: Bob\nName 3: Charlie');
```

## 测试建议

### 1. **基础功能测试**
```python
# Hello World (仍然有效)
print("Hello, World!")
```

### 2. **多行输入测试**
```python
# 多行数字求和
n = int(input())
for i in range(n):
    numbers = list(map(int, input().split()))
    print(sum(numbers))
```

### 3. **多行输出测试**
```python
# 格式化输出
names = input().split()
for i, name in enumerate(names, 1):
    print(f"Name {i}: {name}")
```

### 4. **边界情况测试**
```python
# 空输入处理
try:
    line = input()
    print(line if line else "")
except:
    print("")
```
