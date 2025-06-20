export const initScrollReveal = () => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        observer.unobserve(entry.target); // unobserve sau khi hiển thị
      }
    });
  }, {
    threshold: 0.1
  });

  const revealElements = document.querySelectorAll('.scroll-reveal');
  revealElements.forEach(el => observer.observe(el));
};