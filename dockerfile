# Use a lightweight OpenJDK image as the base
FROM openjdk:17-jdk-alpine

# Optional: Set a maintainer label
# LABEL maintainer="yourname@example.com"

# Install development tools
RUN apk update && apk add --no-cache --update \
    vim \
    fzf \
    unzip \
    busybox-extras \
    openssh \
    curl \
    zsh \
    git \
    bat \
    tmux \
    sed \
    zsh-vcs

# Set vim as the default editor
# RUN echo 'export EDITOR=vim' >> /etc/bashrc

# Set zsh as the default shell for all users
# RUN echo 'chsh -s /usr/bin/zsh root' >> /root/.bashrc
# RUN echo 'chsh -s /usr/bin/zsh appuser' >> /etc/passwd.chsh  # Assuming appuser is your application user

# Create symbolic links for vi and ll
# RUN ln -s /usr/bin/vim /usr/bin/vi && ln -s /usr/bin/ls -l /usr/bin/ll

# Set zsh as default shell
# RUN echo "/bin/zsh" >> /etc/passwd

# Make zsh available as a login shell
RUN echo "/bin/zsh" >> /etc/shells

# Set zsh as default shell for root and appuser
# RUN usermod --shell /bin/zsh root
# no usermod command in Alpine, so we use sed to change the default shell
RUN sed -i 's|/bin/ash|/bin/zsh|' /etc/passwd

# If you have another user
# RUN useradd -m appuser && usermod --shell /bin/zsh appuser

# Set zsh as default shell for new users
# RUN sed -i 's|^SHELL=.*|SHELL=/bin/zsh|' /etc/default/useradd
# no /etc/default/useradd in Alpine, so we set it directly per user
# RUN adduser -D -s /bin/zsh newuser
# or automatedly set it for all users after user creation
# RUN sed -i 's#/bin/ash#/bin/zsh#g' /etc/passwd

RUN curl -L https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh | sh

# Configure bat as default pager (optional)
RUN echo "alias ll='ls -l | bat --color=always'" >> ~/.bashrc

RUN ["/bin/sh", "-c", "sed -i 's/robbyrussell/agnoster/g' ~/.zshrc"]

# alias commands
RUN ["/bin/sh", "-c", "echo alias ll=\"'ls -l | bat --color=always'\" >> /etc/bashrc"]
RUN ["/bin/sh", "-c", "echo alias vi=\"'vim'\" >> /etc/bashrc"]
RUN ["/bin/sh", "-c", "echo alias ll=\"'ls -al | bat --color=always'\" >> ~/.zshrc"]
RUN ["/bin/sh", "-c", "echo alias vi=\"'nvim'\" >> ~/.zshrc"]
RUN echo "alias baf='find / -type f | fzf --preview=\"bat --color=always {}\"'" >> ~/.zshrc
RUN echo "alias vif='nvim \$(find / -type f | fzf --preview=\"bat --color=always --hidden {}\")'" >> ~/.zshrc
# RUN echo "cat ~/.zshrc" > ~/rslt.txt

# RUN source ~/.oh-my-zsh/themes/agnoster.zsh-theme
# RUN source ~/.zshrc
RUN ["/bin/zsh", "-c", "source ~/.zshrc"]

# Set working directory
WORKDIR /app

# Copy your Spring Boot application code
COPY . .

# Expose the port your app runs on (default is 8080)
EXPOSE 8080

# Run the application
# ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["./gradlew", "bootRun", "--continuous", "--watch-fs"]
