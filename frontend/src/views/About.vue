<template>
  <StickyNavbar/>

  <img src="/about.png" alt="Banner Image" class="banner-image" />

  <div class="text-container">
    <div class="text">
      <h1 style="margin: 50px;margin-left: 0;">我们的项目</h1>
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
  { id: 'section1', htmlf: '<h2>', htmll: '</h2>', title: '项目背景' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;随着软件开发技术的快速发展，传统的编程教学方法面临着诸多挑战，尤其是在日新月异的技术环境下。如何有效提升学生的编程能力和团队协作能力，已成为教育工作者亟待解决的问题。传统的教学与非实时编程工具存在直观可操作性和实时反馈性低、代码整合和冲突解决困难、人与人之间沟通效率低等问题。而实时协同编程作为一种新兴的教学与协作模式，能够有效促进学生间的互动交流，提高学习效率和趣味性，完美解决上述问题，具有传播高效、合并协作、强耦合、迭代快等优势。"},
  { id: 'section2', htmlf: '<h2>', htmll: '</h2>',title: '项目介绍' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;基于此，我们决定设计并实现一个专为教学场景打造的实时协同编程环境，旨在为教师和学生提供一个更加灵活高效的编程学习平台。本项目将重点探索和解决教育环境下实时协同编程的关键问题，以满足不同学习需求和教学场景的多样性。"},
  { id: 'section3', htmlf: '<h2>', htmll: '</h2>',title: '功能概述' ,content: ""},
  { id: 'section4', htmlf: '<h3>', htmll: '</h3>',title: '课堂教学' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;在实时协同编程环境中，教师可以即时演示代码示例，学生能够跟随教学内容实时进行编程实践，突破地域限制，实现无缝学习。</p><p>&nbsp;&nbsp;&nbsp;&nbsp;学生不仅可以跟上教师当前正在编辑的代码部分，还能自由探索相关代码文件的其他部分，例如查看正在编辑文件的剩余内容或参考其他相关文件。教师的所有编辑动作，包括修改代码、整理文件结构等，都会立即呈现在所有学生设备上，确保教学过程流畅无延迟。</p><p>&nbsp;&nbsp;&nbsp;&nbsp;教师演示后，可轻松切换到互动模式，授权每位学生在自己的电脑上对刚演示的代码进行编辑、调试和运行，每位学生的编辑活动相互独立、互不干扰。此外，教师可以在课堂上发布编程练习，并实时监控每位学生的编程进度。</p>"},
  { id: 'section5', htmlf: '<h3>', htmll: '</h3>',title: '作业协作' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;学生可以协作编辑作业代码，共同讨论改进，提升作业质量和学习效果。教师可以实时查看学生作业的编辑状态，及时给予反馈和指导。</p><p>&nbsp;&nbsp;&nbsp;&nbsp;在小组协作界面中，可以直观看到每位组员对小组的贡献情况、每位用户离开前的文件备份情况，组长可以管理小组成员和展示用户编辑情况。组长可以管理小组成员的功能体现了不同角色下的功能多样性。学生可以协作编辑作业代码，提高作业质量和学习效果。教师可以实时查看学生作业的编辑状态，查看贡献比例。</p>"},
  { id: 'section6', htmlf: '<h3>', htmll: '</h3>',title: '团队竞赛' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;除提供作业协作外，还可以进行团队竞赛，增加OJ(online judge)平台判题功能。如ACM竞赛需要三个人共同完成题目要求，若不断翻页寻找并编辑对应部分，则会面临代码同步滞后和无法同时工作的问题。这意味着浪费时间且效率低下。而协同竞赛不需要滚动文件查看不同部分，共同完成题目后提交结果，判断代码正确性。这样可以使变更立即传播和合并，更好地支持紧密耦合的协作，快速高效。</p>"},
  { id: 'section7', htmlf: '<h3>', htmll: '</h3>',title: '代码互评' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;在代码教学中，教师可能会要求学生互相评价对方的代码，讲解彼此的优缺点，帮助提高代码规范，同时也增强自身的代码规范意识。</p><p>&nbsp;&nbsp;&nbsp;&nbsp;系统支持学生间的代码互评机制，允许学生分享自己的代码供他人实时评价；同时系统能自动生成多种代码评价样本，从多个维度评价学生的编程作业，有助于营造严谨又富有启发性的学习交流氛围。</p><p>&nbsp;&nbsp;&nbsp;&nbsp;当学生互相评价对方代码时，允许在代码行上添加批注，便于指导和评论。在代码互评页面，学生可以查看评价次数和全部评价，点击参与评价的用户可查看单个学生的批注列表，点击可定位批注书签展开显示。</p>"},
  { id: 'section8', htmlf: '<h2>', htmll: '</h2>',title: '项目展望' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;通过打造一个普适性强的编程教育环境，我们希望充分发挥协同编程在传统课堂、线上教学和自学等各种情境下的优势，提升学生的参与度和学习效果。我们期待这个项目不仅能提升编程教育的实时性和智能化水平，还能创造一个更具包容性、互动性和趣味性的学习环境，吸引更多不同背景和兴趣的学习者，推动编程教育的普及和发展。"},

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
  width: 80%; 
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
  top: 70px;
  right: 10px;
  z-index: 2;
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
  color: #4eaae9;
  font-weight: 700;
}


</style>