<template>
    <StickyNavbar/>
  
    <img src="/about.png" alt="Banner Image" class="banner-image" />
  
    <div class="text-container">
      <div class="text">
        <h1 style="margin: 50px;margin-left: 0;">如何协同？</h1>
        <nav class="article-nav">
          <ul>
            <!-- 动态生成导航链接 -->
            <li v-for="(section, index) in sections" :key="index" 
                :class="{ active: activeSection === section.id }"
                @click="scrollToSection(section.id)">
              <a :href="`#${section.id}`">{{ section.title }}</a>
            </li>
          </ul>
        </nav>
        <section v-for="(section, index) in sections" :key="index" :id="section.id">
          <p v-html="section.htmlf+section.title+section.htmll"></p>
          <p v-html="section.content"></p>
          <br><br>
        </section>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, onUnmounted } from 'vue';
  import StickyNavbar from '../components/Navbar.vue';
  
  // 定义章节数据
  const sections = ref([
    {
        id: 'section1',
        htmlf: '<h2>',
        htmll: '</h2>',
        title: 'OT算法基础原理',
        content: 'OT（操作转换）算法的核心思想是维护协同编辑的一致性，这一技术在我们的项目中起着关键作用。本文档旨在介绍OT算法的基本概念。'
    },
    {
        id: 'section2',
        htmlf: '<h2>',
        htmll: '</h2>',
        title: 'OT算法的持久化思想',
        content: '我们可以通过示例来更简单地理解这一概念：\n\n假设初始文本为"abc"，该文本被复制到两个协同编辑站点。在两个站点上同时产生两个操作：O1 = Insert[0, "x"]（在位置0插入字符"x"）和O2 = Delete[2, 1]（删除位置2的1个字符，即"c"），分别由站点1和站点2的用户产生。在OT算法控制下，本地操作直接执行，而远程操作需经转换后执行。\n\n具体步骤如下：\n1. 在站点1，先执行O1使文档变为"xabc"。当O2到达时，由于受O1影响必须进行转换。O2被转换为O2\' = T(O2, O1) = Delete[3, 1]，其位置参数因并发操作O1的字符插入而增加1。对"xabc"执行O2\'将正确删除"c"，得到文档"xab"。（注：若直接执行原始O2，将错误删除"b"而非"c"）\n2. 在站点2，先执行O2使文档变为"ab"。当O1到达时，被转换为O1\' = T(O1, O2) = Insert[0, "x"]。此时转换后的操作O1\'与原始O1相同，因为O2的执行不影响O1。对"ab"执行O1\'将在位置0插入"x"，得到文档"xab"，与站点1的文档保持一致。\n\n综上，OT的核心思想是：编辑操作在已执行的并发操作影响下被转换为新形式，这既保证了正确的编辑效果（保持意图），又确保副本文档最终一致（收敛性）。'
    },
    {
        id: 'section3',
        htmlf: '<h2>',
        htmll: '</h2>',
        title: 'OT算法的撤销思想',
        content: 'OT算法支持"撤销"的基本思想可通过简单文本编辑场景说明：\n\n假设初始文本为"123"，在两个协同站点复制。该场景发生以下操作：\n1. 步骤1：站点2用户产生操作O1 = Insert[2, "y"]（位置2插入"y"），文档状态更新为"12y"。O1随后传输到站点1并原样执行，使两站点文档状态一致："12y"\n2. 步骤2：站点1用户产生操作O2 = Insert[0, "x"]（位置0插入"x"），文档状态更新为"x12y"。O2随后传输到站点2并原样执行，保持文档状态一致："x12y"\n3. 步骤3：执行O1和O2后，站点2用户发出撤销命令Undo(O1)（O1并非最后执行的操作）。在基于OT的撤销系统中，首先创建O1的逆操作：!O1 = Inverse(O1 = Insert[2, "y"]) = Delete[2]。接着!O1针对O2进行转换，得到!O\'1 = T(!O1, O2) = Delete[3]。执行!O\'1使文档状态更新为"x12"，实现正确撤销效果——消除O1影响同时保留O2效果。若直接执行原始逆操作!O1 = Delete[2]，文档将错误变为"x1y"，这不是预期结果。\n\n总结而言，OT撤销机制通过将待撤销操作O的逆操作转换为新形式来实现正确撤销效果。正确的撤销效果应消除O的影响，同时保留其他操作的影响。换言之，正确撤销效果应将文档状态变为：假设O从未执行，而其他操作都执行过的状态。'
    },
    {
        id: 'section4',
        htmlf: '<h2>',
        htmll: '</h2>',
        title: 'OT算法的操作压缩思想',
        content: 'OT算法的操作压缩基本思想可通过简单文本编辑场景说明：\n\n假设初始文本为"123"，在两个协同站点同步。在站点1，用户依次产生四个操作：O1 = Insert[2, "X"]（位置2插入"X"）、O2 = Insert[1, "abc"]（位置1插入"abc"）、O3 = Insert[2, "Y"]（位置2插入"Y"）和O4 = Delete[7]（删除位置7的"X"）。这些操作被记录在操作日志L = [O1, O2, O3, O4]中但未立即传播。操作压缩算法在将L传播到站点2前对其压缩。\n\n压缩过程如下：\n1. 步骤1：最右侧操作O4与相邻操作O3转置：transpose(O3, O4) = [O\'4, O\'3]，其中O\'4 = Delete[6]，O\'3保持O3不变，得到L\' = [O1, O2, O\'4, O3]\n2. 步骤2：O\'4与新相邻操作O2转置：transpose(O2, O\'4) = [O\'\'4, O\'2]，其中O\'\'4 = Delete[3]，O\'2保持O2不变，得到L\' = [O1, O\'\'4, O2, O3]\n3. 步骤3：检查O\'\'4与新相邻操作O1，发现二者互补且重叠（即对文档无影响），将O\'\'4和O1从L中移除，得到L\' = [O2, O3]\n4. 步骤4：将L\'中的重叠操作O2和O3合并为单一操作O\'2 = Insert[1, "aYbc"]，最终得到L\' = [O\'2]\n5. 最终结果L\' = [O\'2]替代原始L = [O1, O2, O3, O4]传播到远程站点进行集成\n\n综上，OT操作压缩的核心思想是：将累积操作转置到日志L中的适当位置和形式，检查重叠或互补关系后决定合并或移除，实现"压缩"。这能在保持L原始效果的前提下减少L中的操作数量。'
    }
]);

  
  const activeSection = ref<string | null>(null);// 当前活跃的 section ID
  
  // 滚动到对应的 section
  const scrollToSection = (id: string) => {
    const sectionElement = document.getElementById(id);
    if (sectionElement) {
      sectionElement.scrollIntoView({ behavior: 'smooth' });
    }
  };
  
  // 监听滚动事件，更新 activeSection
  const updateActiveSection = () => {
    const sectionElements = sections.value.map(section => 
      document.getElementById(section.id)
    );
  
    // 找到当前可视范围内的 section
    for (const sectionElement of sectionElements) {
      if (sectionElement) {
        const rect = sectionElement.getBoundingClientRect();
        if (rect.top >= 0 && rect.top < window.innerHeight / 2) {
          activeSection.value = sectionElement.id;
          break;
        }
      }
    }
  };
  
  onMounted(() => {
    window.addEventListener('scroll', updateActiveSection);
  
    // 清除事件监听器
    onUnmounted(() => {
      window.removeEventListener('scroll', updateActiveSection);
    });
  });
  </script>
  
  <style scoped>
  
  .banner-image {
    width: auto; 
    height: 100%; 
    border-bottom-left-radius: 30px;
    border-bottom-right-radius: 30px;
  }
  
  .text-container {
    display: flex;
    margin-left: 100px;
  }
  
  .text {
    width: 60%;
  }
  
  .article-nav {
    position: fixed;
    top: 50px;
    right: 10px;
    z-index: 1000;
    width: 250px;
  }
  
  .article-nav ul {
    list-style: none;
    padding: 0;
  }
  
  .article-nav ul li {
    padding: 20px;
    cursor: pointer; /* 添加手形指针 */
  }
  
  .article-nav ul li:hover {
    background-color: rgba(175, 175, 175, 0.3);
    border-radius: 10px;
  }
  
  .article-nav ul li.active {
    background-color: rgba(225, 225, 225, 0.6);
    border-radius: 10px;
  }
  
  a {
    color: inherit; /* 继承父级颜色 */
    text-decoration: none;
  }
  
  li.active a {
    color: #CBA5D1;
    font-weight: 700;
  }
  
  
  </style>