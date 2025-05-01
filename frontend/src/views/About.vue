<template>
  <StickyNavbar/>

  <img src="/about.png" alt="Banner Image" class="banner-image" />

  <div class="text-container">
    <div class="text">
      <h1 style="margin: 50px;margin-left: 0;">Our Works</h1>
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
  { id: 'section1', htmlf: '<h2>', htmll: '</h2>', title: 'Project Background' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;With the rapid development of software development technology, traditional programming teaching methods face numerous challenges, especially in the rapidly changing technological environment. How to effectively enhance students' programming capabilities and team collaboration skills has become an urgent issue for educators. Traditional teaching and non-real-time programming tools suffer from low intuitive operability and real-time feedback, difficulties in code integration and conflict resolution, and low communication efficiency between people. However, real-time collaborative programming, as an emerging teaching and collaboration model, can effectively promote interaction and communication among students, improve learning efficiency and enjoyment, and perfectly solve the above problems, with advantages such as efficient dissemination, merged collaboration, strong coupling, and high iteration speed."},
  { id: 'section2', htmlf: '<h2>', htmll: '</h2>',title: 'Project Introduction' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;Based on this, we have decided to design and implement a real-time collaborative programming environment specifically for teaching scenarios, aiming to provide teachers and students with a more flexible and efficient platform for learning programming. This project will focus on exploring and solving key issues in real-time collaborative programming in educational environments to meet the diversity of different learning needs and teaching scenarios."},
  { id: 'section3', htmlf: '<h2>', htmll: '</h2>',title: 'Functional Overview' ,content: ""},
  { id: 'section4', htmlf: '<h3>', htmll: '</h3>',title: 'Classroom Teaching' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;In the real-time collaborative programming environment, teachers can instantly demonstrate code examples, and students can follow the teaching content in real-time for programming practice, overcoming geographical restrictions and achieving seamless learning.</p><p>&nbsp;&nbsp;&nbsp;&nbsp;Students can not only keep up with the code sections that the teacher is currently editing but also freely explore other parts of the related code files, such as viewing the remaining content of the file being edited or referring to other related files. All editing actions of the teacher, including modifying code and organizing file structures, will be immediately presented on all student devices, ensuring a smooth and delay-free teaching process.</p><p>&nbsp;&nbsp;&nbsp;&nbsp;After the teacher's demonstration, it is easy to switch to an interactive mode, authorizing each student to edit, debug, and run the code just demonstrated on their own computers, with each student's editing activities being independent and non-interfering. Additionally, teachers can publish programming exercises in class and monitor each student's programming progress in real-time.</p>"},
  { id: 'section5', htmlf: '<h3>', htmll: '</h3>',title: 'Homework Collaboration' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;Students can collaborate on editing homework code, discuss and improve together, enhancing the quality of homework and learning outcomes. Teachers can view the editing status of student homework in real-time and provide feedback and guidance promptly.</p><p>&nbsp;&nbsp;&nbsp;&nbsp;In the group collaboration interface, it is intuitive to see each team member's contribution to the group, file backups before each user leaves, and the group leader can manage team members and display user editing situations. The ability of the group leader to manage team members demonstrates the functional diversity from different roles. Students can collaborate on editing homework code, improving the quality of homework and learning outcomes. Teachers can view the editing status of student homework in real-time and check contribution ratios.</p>"},
  { id: 'section6', htmlf: '<h3>', htmll: '</h3>',title: 'Team Competitions' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;In addition to providing homework collaboration, team competitions can also be conducted, with the addition of OJ (online judge) platform judging functionality. For example, ACM competitions require three people to complete the problem requirements. If they constantly flip through pages to find and edit the corresponding parts, they will face the problem of code synchronization lag and the inability to work simultaneously. This means wasting time and low efficiency. However, collaborative competitions do not require scrolling through files to view different parts. After jointly completing the problem, submit the results and judge the correctness of the code. This allows changes to be immediately propagated and merged, better supporting closely coupled collaboration, and quickly and efficiently.</p>"},
  { id: 'section7', htmlf: '<h3>', htmll: '</h3>',title: 'Code Review' ,content: "<p>&nbsp;&nbsp;&nbsp;&nbsp;In code teaching, teachers may ask students to review each other's code, explaining the strengths and weaknesses of each other to help improve code standards, while also enhancing their own awareness of code standards.</p><p>&nbsp;&nbsp;&nbsp;&nbsp;The system supports a code review mechanism between students, allowing students to share their code for others to review in real-time; at the same time, the system can automatically generate a variety of code evaluation samples, evaluating students' programming assignments from multiple dimensions, helping to create a rigorous and inspiring learning and communication atmosphere.</p><p>&nbsp;&nbsp;&nbsp;&nbsp;When students review each other's code, they are allowed to add annotations to the code lines for easy guidance and comments. On the code review page, students can view the number of evaluations and all evaluations, click on the users who participated in the evaluation to view the annotation list of individual students, and click to locate the annotation bookmark to expand and display.</p>"},
  { id: 'section8', htmlf: '<h2>', htmll: '</h2>',title: 'Project Expectations' ,content: "&nbsp;&nbsp;&nbsp;&nbsp;By creating a universally strong programming education environment, we hope to fully leverage the advantages of collaborative programming in traditional classrooms, online teaching, and self-study in various situations, enhancing student participation and learning outcomes. We look forward to this project not only improving the real-time and intelligent level of programming education but also creating a more inclusive, interactive, and interesting learning environment, attracting more learners from different backgrounds and interests, and promoting the popularization and development of programming education."},

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
  color: #CBA5D1;
  font-weight: 700;
}


</style>